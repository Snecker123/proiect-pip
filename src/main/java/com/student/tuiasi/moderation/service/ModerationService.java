package com.student.tuiasi.moderation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ModerationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.token}")
    private String apiToken;

    private static final double TOXICITY_THRESHOLD = 0.5;

    public ModerationResult analyzeText(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");

            String requestBody = "{\"inputs\": \"" + text.replace("\"", "\\\"") + "\"}";
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode inner = root.get(0);

            for (JsonNode item : inner) {
                String label = item.get("label").asText();
                double score = item.get("score").asDouble();

                if ("toxicity".equalsIgnoreCase(label)) {
                    boolean isBlocked = score >= TOXICITY_THRESHOLD;
                    System.out.println("[Moderation] Text analizat | Label: "
                        + label + " | Score: " + score
                        + " | Decizie: " + (isBlocked ? "BLOCAT" : "APROBAT"));
                    return new ModerationResult(isBlocked, score, label);
                }
            }

        } catch (Exception e) {
            System.err.println("[Moderation] Eroare la apelul HuggingFace: " + e.getMessage());
        }

        return new ModerationResult(false, 0.0, "ERROR");
    }

    public record ModerationResult(boolean blocked, double confidence, String label) {}
}