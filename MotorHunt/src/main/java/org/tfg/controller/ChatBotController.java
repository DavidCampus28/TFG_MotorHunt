package org.tfg.controller;

import org.tfg.dto.ChatRequest;
import org.tfg.dto.ChatResponse;
import org.tfg.service.ChatBotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@AllArgsConstructor
@Slf4j
public class ChatBotController {

    private final ChatBotService chatBotService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            log.info("Pregunta recibida: {}", request.getPrompt());

            String response = chatBotService.askQuestion(request.getPrompt());

            return ResponseEntity.ok(new ChatResponse(response, true, null));
        } catch (Exception e) {
            log.error("Error en el chatbot", e);
            return ResponseEntity.ok(new ChatResponse(
                "Lo siento, ocurrió un error al procesar tu pregunta. Intenta de nuevo.",
                false,
                e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ChatBot está funcionando correctamente");
    }
}

