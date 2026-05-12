package com.student.tuiasi.moderation.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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