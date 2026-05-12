package com.student.tuiasi.moderation.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Serviciu pentru trimiterea notificarilor prin email.
 * Foloseste Gmail SMTP prin JavaMailSender pentru a notifica
 * parintii cand continutul copilului este blocat de sistemul
 * de moderare.
 */
@Service
public class NotificationService {

    /** Client pentru trimiterea emailurilor prin Gmail SMTP. */
    private final JavaMailSender mailSender;

    /**
     * Constructorul serviciului.
     *
     * @param mailSender clientul de email configurat automat de Spring
     */
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Trimite un email de notificare parintelui cand un text
     * postat de copil este blocat de sistemul de moderare.
     * Emailul contine username-ul copilului si textul blocat.
     *
     * @param parentEmail adresa de email a parintelui
     * @param childUsername username-ul copilului care a postat
     * @param blockedText textul care a fost blocat
     */
    public void notifyParentTextBlocked(String parentEmail, String childUsername, String blockedText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(parentEmail);
        message.setSubject("[Moderation Alert] Continut blocat pentru " + childUsername);
        message.setText(
            "Buna ziua,\n\n" +
            "Contul copilului dumneavoastra '" + childUsername + "' a incercat sa posteze continut inadecvat.\n\n" +
            "Textul blocat:\n\"" + blockedText + "\"\n\n" +
            "Continutul a fost blocat automat de sistemul de moderare.\n\n" +
            "Moderation Service"
        );
        mailSender.send(message);
        System.out.println("[Notification] Email trimis la: " + parentEmail);
    }
}