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

/**
 * Controller pentru endpoint-urile de moderare a continutului.
 * Expune endpoint-uri pentru analiza manuala a textului
 * si pentru verificarea starii serviciului.
 * Acest controller este destinat administratorilor si testarii —
 * utilizatorii finali folosesc PostController pentru postari.
 */
@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

    /** Serviciu pentru moderarea textului. */
    private final ModerationService moderationService;

    /** Serviciu pentru accesul la conturile de copii. */
    private final ChildAccountService childAccountService;

    /** Serviciu pentru accesul la conturile de parinti. */
    private final AccountService accountService;

    /** Serviciu pentru trimiterea notificarilor. */
    private final NotificationService notificationService;

    /**
     * Constructorul controller-ului.
     *
     * @param moderationService serviciul de moderare
     * @param childAccountService serviciul conturilor de copii
     * @param accountService serviciul conturilor de parinti
     * @param notificationService serviciul de notificari
     */
    public ModerationController(ModerationService moderationService,
                                 ChildAccountService childAccountService,
                                 AccountService accountService,
                                 NotificationService notificationService) {
        this.moderationService = moderationService;
        this.childAccountService = childAccountService;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    /**
     * Analizeaza un text pentru toxicitate.
     * Daca textul este blocat si un childId este furnizat,
     * parintele este notificat prin email.
     *
     * @param request requestul cu textul de analizat si optional childId
     * @return decizia de moderare cu scorul si label-ul
     */
    @PostMapping("/analyze")
    public ResponseEntity<ModerationResponse> analyzeText(@RequestBody ModerationRequest request) {
        ModerationResult result = moderationService.analyzeText(request.getText());
        String decision = result.blocked() ? "BLOCKED" : "SAFE";

        if (result.blocked() && request.getChildId() != null) {
            notifyParent(request.getChildId(), request.getText());
        }

        return ResponseEntity.ok(new ModerationResponse(decision, result.confidence(), result.label()));
    }

    /**
     * Verifica daca serviciul ruleaza.
     *
     * @return mesaj de confirmare
     */
    @GetMapping("/health")
    public String health() {
        return "Service is running!";
    }

    /**
     * Notifica parintele unui copil cand un text este blocat.
     * Daca copilul sau parintele nu sunt gasiti, metoda returneaza fara eroare.
     *
     * @param childId identificatorul copilului
     * @param blockedText textul care a fost blocat
     */
    private void notifyParent(int childId, String blockedText) {
        Optional<ChildAccount> childOpt = childAccountService.getById(childId);
        if (childOpt.isEmpty()) return;

        ChildAccount child = childOpt.get();
        Optional<ParentAccount> parentOpt = accountService.getById(child.getParentId());
        if (parentOpt.isEmpty()) return;

        notificationService.notifyParentTextBlocked(
                parentOpt.get().getEmail(), child.getUsername(), blockedText);
    }

    /**
     * Reprezinta requestul de moderare a unui text.
     */
    public static class ModerationRequest {
        /** Textul de analizat. */
        private String text;

        /** Identificatorul utilizatorului — neutilizat in prezent. */
        private String userId;

        /** Identificatorul copilului pentru notificarea parintelui. */
        private Integer childId;

        /**
         * Returneaza textul de analizat.
         *
         * @return textul
         */
        public String getText() { return text; }

        /**
         * Seteaza textul de analizat.
         *
         * @param text textul
         */
        public void setText(String text) { this.text = text; }

        /**
         * Returneaza identificatorul utilizatorului.
         *
         * @return userId
         */
        public String getUserId() { return userId; }

        /**
         * Seteaza identificatorul utilizatorului.
         *
         * @param userId identificatorul
         */
        public void setUserId(String userId) { this.userId = userId; }

        /**
         * Returneaza identificatorul copilului.
         *
         * @return childId
         */
        public Integer getChildId() { return childId; }

        /**
         * Seteaza identificatorul copilului.
         *
         * @param childId identificatorul
         */
        public void setChildId(Integer childId) { this.childId = childId; }
    }

    /**
     * Reprezinta raspunsul moderarii unui text.
     */
    public static class ModerationResponse {
        /** Decizia de moderare — BLOCKED sau SAFE. */
        private String decision;

        /** Scorul de incredere al modelului. */
        private double confidence;

        /** Label-ul returnat de model. */
        private String label;

        /**
         * Constructorul raspunsului.
         *
         * @param decision decizia de moderare
         * @param confidence scorul de incredere
         * @param label label-ul modelului
         */
        public ModerationResponse(String decision, double confidence, String label) {
            this.decision = decision;
            this.confidence = confidence;
            this.label = label;
        }

        /**
         * Returneaza decizia de moderare.
         *
         * @return decizia
         */
        public String getDecision() { return decision; }

        /**
         * Returneaza scorul de incredere.
         *
         * @return scorul
         */
        public double getConfidence() { return confidence; }

        /**
         * Returneaza label-ul modelului.
         *
         * @return label-ul
         */
        public String getLabel() { return label; }
    }
}