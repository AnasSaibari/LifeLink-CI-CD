package org.example.chatservice.Controller;

import org.example.chatservice.DTO.MessageDTO;
import org.example.chatservice.DTO.MessageRequestDTO;
import org.example.chatservice.Service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getAllMessagesByConversationId(@PathVariable Long conversationId) {
        log.info("Request GET /messages/conversation/{} - Fetching all messages for conversation", conversationId);
        
        List<MessageDTO> messages = messageService.getAllMessagesByConversationId(conversationId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageRequestDTO requestDTO) {
        log.info("Request POST /messages - Creating message for conversation id: {} with senderRole: {}", 
                requestDTO.getConversationId(), requestDTO.getSenderRole());
        
        MessageDTO createdMessage = messageService.createMessage(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }
}
