package org.tfg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String question;
    private String answer;
    private String role; // "user" o "assistant"
    private Long timestamp;
}

