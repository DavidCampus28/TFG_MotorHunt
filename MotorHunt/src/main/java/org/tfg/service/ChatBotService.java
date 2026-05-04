package org.tfg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class ChatBotService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_API_KEY = "gsk_ZW9rUWRWZTB4eWJ3ZkJrNW5JUVE1N0ZQdW1GZW1SZUdFWkFrcjBDSwD";
    
    public ChatBotService() {
    }

    public String askQuestion(String question) {
        try {
            log.info("Pregunta: {}", question);
            return consultarGroq(question);
        } catch (Exception e) {
            log.error("Error Groq", e);
            return respuestaLocal(question);
        }
    }

    private String consultarGroq(String pregunta) throws Exception {
        URL url = new URL(GROQ_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + GROQ_API_KEY);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);
        conn.setDoOutput(true);

        String jsonPayload = "{" +
            "\"model\": \"mixtral-8x7b-32768\"," +
            "\"messages\": [{\"role\": \"system\", \"content\": \"Eres experto en vehículos. Responde sobre coches de forma útil y precisa. Sé directo y conciso.\"}," +
            "{\"role\": \"user\", \"content\": \"" + pregunta.replace("\"", "\\\"") + "\"}]," +
            "\"max_tokens\": 500," +
            "\"temperature\": 0.3" +
            "}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }

        if (conn.getResponseCode() == 200) {
            String response = new String(conn.getInputStream().readAllBytes());
            JsonNode root = objectMapper.readTree(response);
            if (root.has("choices") && root.get("choices").size() > 0) {
                return root.get("choices").get(0).get("message").get("content").asText().trim();
            }
        }

        return respuestaLocal(pregunta);
    }

    private String respuestaLocal(String pregunta) {
        String q = pregunta.toLowerCase();

        if (q.contains("1.9") && q.contains("tdi")) {
            return "Motor 1.9 TDI (Diesel):\n" +
                   "- Potencia: 90-150 CV\n" +
                   "- Torque: 210-320 Nm\n" +
                   "- Consumo: 5-7 L/100km\n" +
                   "- Compatible: VW Golf, Passat, Bora, Beetle; Audi A4, A6; Skoda Octavia; Seat Leon\n" +
                   "⚠️ Cambiar correa distribución cada 160k km (importante)";
        }

        if (q.contains("1.6") && (q.contains("16v") || q.contains("206"))) {
            return "Motor 1.6 16V Peugeot 206:\n" +
                   "- Potencia: 110 CV\n" +
                   "- Torque: 148 Nm\n" +
                   "- Consumo: 7-9 L/100km\n" +
                   "- Año: 2003-2009\n" +
                   "- Problemas comunes: Junta culata, sensor MAF, correa distribución\n" +
                   "- Mantenimiento: Cada 15k km aceite, 60k km correa";
        }

        if (q.contains("coches") && (q.contains("1.9 tdi") || q.contains("montan"))) {
            return "Coches con motor 1.9 TDI:\n" +
                   "VW: Golf IV-V, Passat, Bora, Beetle\n" +
                   "Audi: A4 B5-B7, A6 C5, TT\n" +
                   "Skoda: Octavia, Fabia, Superb\n" +
                   "Seat: Leon, Toledo, Alhambra\n" +
                   "Ford: Galaxy\n" +
                   "Años: 1998-2010";
        }

        return "Sé más específico. Pregunta marca, modelo, año.";
    }
}

