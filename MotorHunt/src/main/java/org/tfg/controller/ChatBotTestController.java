package org.tfg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatBotTestController {

    @GetMapping("/chatbot-test")
    public String chatbotTest() {
        return "chatbot-test";
    }
}

