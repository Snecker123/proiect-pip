package com.student.tuiasi.moderation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Serviciu pentru moderarea textului folosind HuggingFace Inference API.
 * Analizeaza textul primit si returneaza un scor de toxicitate.
 * Textele cu scor peste pragul definit sunt marcate ca blocate.
 */
@Service
public class ModerationService {

    /** Client HTTP pentru apelarea API-ului HuggingFace. */
    private final RestTemplate restTemplate = new RestTemplate();

    /** Obiect pentru parsarea raspunsurilor JSON. */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** URL-ul endpoint-ului de inferenta HuggingFace. */
    @Value("${huggingface.api.url}")
    private String apiUrl;

    /** Token-ul de autentificare pentru HuggingFace API. */
    @Value("${huggingface.api.token}")
    private String apiToken;

    /**
     * Pragul de toxicitate peste care un text este blocat.
     * Valori intre 0.0 si 1.0.
     */
    private static final double TOXICITY_THRESHOLD = 0.5;

    /**
     * Analizeaza un text pentru toxicitate folosind HuggingFace API.
     * In caz de eroare la apelul API, returneaza un rezultat de tip ERROR
     * cu blocked = false.
     *
     * @param text textul de analizat
     * @return rezultatul moderarii cu decizia, scorul si label-ul
     */
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

    /**
     * Reprezinta rezultatul analizei de moderare a unui text.
     *
     * @param blocked true daca textul este blocat, false altfel
     * @param confidence scorul de incredere al modelului
     * @param label label-ul returnat de model
     */
    public record ModerationResult(boolean blocked, double confidence, String label) {}
}