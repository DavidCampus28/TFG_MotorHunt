package org.tfg.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatBotService {

    public ChatBotService() {
    }

    public String askQuestion(String question) {
        try {
            log.info("Pregunta recibida: {}", question);
            return respuestaDummy();
        } catch (Exception e) {
            log.error("Error", e);
            return respuestaDummy();
        }
    }

    private String respuestaDummy() {
        return "Disculpa, el chatbot está en mantenimiento. " +
               "Por favor contacta directamente con el vendedor.";
    }
}

