package org.example.chatservice.DTO;

import lombok.Data;

@Data
public class MessageRequestDTO {
    private String senderRole; // "user" or "hospital"
    private String text;
    private Long conversationId; // ID de la conversation
}

