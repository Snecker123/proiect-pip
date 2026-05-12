package com.student.tuiasi.moderation.controller;

import com.student.tuiasi.moderation.model.ChildAccount;
import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.service.ModerationService;
import com.student.tuiasi.moderation.service.ModerationService.ModerationResult;
import com.student.tuiasi.moderation.service.ChildAccountService;
import com.student.tuiasi.moderation.service.AccountService;
import com.student.tuiasi.moderation.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

    private final ModerationService moderationService;
    private final ChildAccountService childAccountService;
    private final AccountService accountService;
    private final NotificationService notificationService;

    public ModerationController(ModerationService moderationService,
                                 ChildAccountService childAccountService,
                                 AccountService accountService,
                                 NotificationService notificationService) {
        this.moderationService = moderationService;
        this.childAccountService = childAccountService;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ModerationResponse> analyzeText(@RequestBody ModerationRequest request) {
        ModerationResult result = moderationService.analyzeText(request.getText());
        String decision = result.blocked() ? "BLOCKED" : "SAFE";

        if (result.blocked() && request.getChildId() != null) {
            notifyParent(request.getChildId(), request.getText());
        }

        return ResponseEntity.ok(new ModerationResponse(decision, result.confidence(), result.label()));
    }

    @GetMapping("/health")
    public String health() {
        return "Service is running!";
    }

    private void notifyParent(int childId, String blockedText) {
        Optional<ChildAccount> childOpt = childAccountService.getById(childId);
        if (childOpt.isEmpty()) return;

        ChildAccount child = childOpt.get();
        Optional<ParentAccount> parentOpt = accountService.getById(child.getParentId());
        if (parentOpt.isEmpty()) return;

        notificationService.notifyParentTextBlocked(
                parentOpt.get().getEmail(), child.getUsername(), blockedText);
    }

    public static class ModerationRequest {
        private String text;
        private String userId;
        private Integer childId;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public Integer getChildId() { return childId; }
        public void setChildId(Integer childId) { this.childId = childId; }
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