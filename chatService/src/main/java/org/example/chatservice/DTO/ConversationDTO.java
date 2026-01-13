package org.example.chatservice.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long id;

    @JsonProperty("sender")
    private PersonInfo sender;

    @JsonProperty("receiver")
    private PersonInfo receiver;

    private String lastMessage;
    private String createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonInfo {
        private String name;
    }
}

