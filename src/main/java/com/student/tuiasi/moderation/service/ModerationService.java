package com.student.tuiasi.moderation.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class ModerationService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // URL-ul corect pentru API call
    private final String apiUrl = "https://api-inference.huggingface.co/models/distilbert-base-uncased-finetuned-sst-2-english";
    
    // Token-ul tău de la Hugging Face
    private final String apiToken = "AiCi pUi ToKeN uL dE aCcEs De La Huggingface"; // NU UITA SA PUI AICIIIIIIIIIIIIIIIIII
    
    public ModerationResult analyzeText(String text) {
        try {
            // 1. Pregătește header-ele
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 2. Pregătește body-ul
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("inputs", text);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            // 3. Trimite request-ul
            ResponseEntity<List> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                List.class
            );
            
            // 4. Parsează răspunsul
            List<?> responseBody = response.getBody();
            if (responseBody != null && !responseBody.isEmpty()) {
                Map<String, Object> firstResult = (Map<String, Object>) responseBody.get(0);
                String label = (String) firstResult.get("label");
                Double score = (Double) firstResult.get("score");
                
                boolean isBlocked = "NEGATIVE".equals(label);
                
                System.out.println("Text: " + text);
                System.out.println("Rezultat: " + label + " (scor: " + score + ")");
                System.out.println("Decizie: " + (isBlocked ? "BLOCAT" : "APROBAT"));
                
                return new ModerationResult(isBlocked, score, label);
            }
        } catch (Exception e) {
            System.err.println("Eroare: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new ModerationResult(false, 0.0, "ERROR");
    }
    
    public record ModerationResult(boolean blocked, double confidence, String label) {}
}