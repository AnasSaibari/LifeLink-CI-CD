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
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
            // Utiliser findOrCreateConversation pour éviter les doublons
            Conversation conversation = findOrCreateConversation(requestDTO.getSenderId(), requestDTO.getReceiverId());
            log.info("Conversation found or created with id: {}", conversation.getId());

            return convertToDTO(conversation);
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

    /**
     * Trouve une conversation existante entre deux participants, ou en crée une nouvelle si elle n'existe pas
     * Utilise @Transactional pour éviter les conditions de course (race conditions)
     * @param senderId ID de l'expéditeur
     * @param receiverId ID du destinataire
     * @return La conversation existante ou nouvellement créée
     */
    @Transactional
    public Conversation findOrCreateConversation(Long senderId, Long receiverId) {
        log.info("Finding or creating conversation between senderId: {} and receiverId: {}", senderId, receiverId);

        // Chercher une conversation existante dans les deux sens
        Optional<Conversation> existingConversation = conversationRepository.findExistingConversation(senderId, receiverId);

        if (existingConversation.isPresent()) {
            log.info("Found existing conversation with id: {}", existingConversation.get().getId());
            return existingConversation.get();
        }

        // Vérifier une deuxième fois avant de créer (pour éviter les conditions de course)
        // Si deux requêtes arrivent en même temps, la deuxième trouvera la conversation créée par la première
        existingConversation = conversationRepository.findExistingConversation(senderId, receiverId);
        if (existingConversation.isPresent()) {
            log.info("Found existing conversation on second check with id: {}", existingConversation.get().getId());
            return existingConversation.get();
        }

        // Créer une nouvelle conversation
        log.info("No existing conversation found, creating new one");
        Conversation newConversation = new Conversation();
        newConversation.setSenderId(senderId);
        newConversation.setReceiverId(receiverId);
        newConversation.setLastMessage(null);

        Conversation savedConversation = conversationRepository.save(newConversation);
        log.info("Created new conversation with id: {}", savedConversation.getId());
        return savedConversation;
    }
}
