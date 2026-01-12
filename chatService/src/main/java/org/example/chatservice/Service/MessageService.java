package org.example.chatservice.Service;

import org.example.chatservice.DTO.MessageDTO;
import org.example.chatservice.DTO.MessageRequestDTO;
import org.example.chatservice.Entity.Message;
import org.example.chatservice.Repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationService conversationService;

    public MessageDTO createMessage(MessageRequestDTO requestDTO) {
        log.info("Creating message for conversation id: {} with senderRole: {}", requestDTO.getConversationId(), requestDTO.getSenderRole());

        try {
            // Verify conversation exists
            conversationService.findById(requestDTO.getConversationId());

            Message message = new Message();
            message.setConversationId(requestDTO.getConversationId());
            message.setSenderRole(requestDTO.getSenderRole());
            message.setText(requestDTO.getText());

            Message savedMessage = messageRepository.save(message);
            log.info("Message created successfully with id: {}", savedMessage.getId());

            // Update last message in conversation
            conversationService.updateLastMessage(requestDTO.getConversationId(), requestDTO.getText());

            return convertToDTO(savedMessage);
        } catch (EntityNotFoundException e) {
            log.warn("Conversation not found with id: {}", requestDTO.getConversationId());
            throw e;
        } catch (Exception e) {
            log.error("Error creating message: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<MessageDTO> getAllMessagesByConversationId(Long conversationId) {
        log.info("Fetching all messages for conversation id: {}", conversationId);

        try {
            // Verify conversation exists
            conversationService.findById(conversationId);

            List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
            log.info("Found {} messages for conversation id: {}", messages.size(), conversationId);

            return messages.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            log.warn("Conversation not found with id: {}", conversationId);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching messages for conversation id {}: {}", conversationId, e.getMessage(), e);
            throw e;
        }
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderRole(message.getSenderRole());
        dto.setText(message.getText());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}
