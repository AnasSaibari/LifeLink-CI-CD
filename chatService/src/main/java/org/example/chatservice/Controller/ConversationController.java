package org.example.chatservice.Controller;

import org.example.chatservice.DTO.ConversationDTO;
import org.example.chatservice.DTO.ConversationRequestDTO;
import org.example.chatservice.Service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationDTO> createConversation(@RequestBody ConversationRequestDTO requestDTO) {
        log.info("Request POST /conversations - Creating conversation with senderId: {} and receiverId: {}", 
                requestDTO.getSenderId(), requestDTO.getReceiverId());
        
        ConversationDTO createdConversation = conversationService.createConversation(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConversation);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationDTO>> getAllConversationsByUserId(@PathVariable Long userId) {
        log.info("Request GET /conversations/user/{} - Fetching all conversations for user", userId);
        
        List<ConversationDTO> conversations = conversationService.getAllConversationsByUserId(userId);
        return ResponseEntity.ok(conversations);
    }
}
