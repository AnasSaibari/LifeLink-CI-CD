package org.example.chatservice.DTO;

import lombok.Data;

@Data
public class ConversationRequestDTO {
    private Long senderId;
    private Long receiverId;
}

