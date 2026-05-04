package org.tfg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String prompt;
    private String context; // Contexto adicional sobre coches
}

