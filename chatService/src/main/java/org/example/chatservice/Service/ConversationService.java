package org.example.chatservice.Service;

import org.example.chatservice.DTO.ConversationDTO;
import org.example.chatservice.DTO.ConversationRequestDTO;
import org.example.chatservice.DTO.HospitalDTO;
import org.example.chatservice.DTO.UserDTO;
import org.example.chatservice.Entity.Conversation;
import org.example.chatservice.FeignClient.HospitalClient;
import org.example.chatservice.FeignClient.UserClient;
import org.example.chatservice.Repository.ConversationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserClient userClient;
    private final HospitalClient hospitalClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ConversationDTO createConversation(ConversationRequestDTO requestDTO) {
        log.info("Creating conversation with senderId: {} and receiverId: {}", requestDTO.getSenderId(), requestDTO.getReceiverId());

        try {
            Conversation conversation = new Conversation();
            conversation.setSenderId(requestDTO.getSenderId());
            conversation.setReceiverId(requestDTO.getReceiverId());
            conversation.setLastMessage(null);

            Conversation savedConversation = conversationRepository.save(conversation);
            log.info("Conversation created successfully with id: {}", savedConversation.getId());

            return convertToDTO(savedConversation);
        } catch (Exception e) {
            log.error("Error creating conversation: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<ConversationDTO> getAllConversationsByUserId(Long userId) {
        log.info("Fetching all conversations for user id: {}", userId);

        try {
            List<Conversation> conversations = conversationRepository.findBySenderIdOrReceiverId(userId, userId);
            log.info("Found {} conversations for user id: {}", conversations.size(), userId);

            return conversations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching conversations for user id {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    private ConversationDTO convertToDTO(Conversation conversation) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setLastMessage(conversation.getLastMessage());
        dto.setCreatedAt(conversation.getCreatedAt() != null ? conversation.getCreatedAt().format(DATE_FORMATTER) : null);

        // Fetch sender name
        try {
            UserDTO userDTO = userClient.getUserById(conversation.getSenderId());
            ConversationDTO.PersonInfo senderInfo = new ConversationDTO.PersonInfo(userDTO.getUsername());
            dto.setSender(senderInfo);
        } catch (Exception e) {
            log.warn("Failed to fetch user for senderId {}: {}", conversation.getSenderId(), e.getMessage());
            dto.setSender(new ConversationDTO.PersonInfo("Unknown"));
        }

        // Fetch receiver name (could be user or hospital)
        try {
            // Try as user first
            UserDTO userDTO = userClient.getUserById(conversation.getReceiverId());
            ConversationDTO.PersonInfo receiverInfo = new ConversationDTO.PersonInfo(userDTO.getUsername());
            dto.setReceiver(receiverInfo);
        } catch (Exception e) {
            try {
                // If user fails, try as hospital
                HospitalDTO hospitalDTO = hospitalClient.getHospitalById(conversation.getReceiverId());
                ConversationDTO.PersonInfo receiverInfo = new ConversationDTO.PersonInfo(hospitalDTO.getHospital_nom());
                dto.setReceiver(receiverInfo);
            } catch (Exception ex) {
                log.warn("Failed to fetch receiver for receiverId {}: {}", conversation.getReceiverId(), ex.getMessage());
                dto.setReceiver(new ConversationDTO.PersonInfo("Unknown"));
            }
        }

        return dto;
    }

    public Conversation findById(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Conversation not found with id: {}", id);
                    return new EntityNotFoundException("Conversation not found with id: " + id);
                });
    }

    public void updateLastMessage(Long conversationId, String lastMessage) {
        Conversation conversation = findById(conversationId);
        conversation.setLastMessage(lastMessage);
        conversationRepository.save(conversation);
        log.debug("Updated last message for conversation id: {}", conversationId);
    }
}
