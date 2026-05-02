package com.student.tuiasi.moderation.controller;

import com.student.tuiasi.moderation.service.ModerationService;
import com.student.tuiasi.moderation.service.ModerationService.ModerationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ModerationResponse> analyzeText(@RequestBody ModerationRequest request) {
        ModerationResult result = moderationService.analyzeText(request.getText());
        String decision = result.blocked() ? "BLOCKED" : "SAFE";
        return ResponseEntity.ok(new ModerationResponse(decision, result.confidence(), result.label()));
    }

    @GetMapping("/health")
    public String health() {
        return "Service is running!";
    }

    public static class ModerationRequest {
        private String text;
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class ModerationResponse {
        private String decision;
        private double confidence;
        private String label;

        public ModerationResponse(String decision, double confidence, String label) {
            this.decision = decision;
            this.confidence = confidence;
            this.label = label;
        }

        public String getDecision() { return decision; }
        public double getConfidence() { return confidence; }
        public String getLabel() { return label; }
    }
}