package com.example.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/test-email")
    public String testEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("jamliamine413@gmail.com");
        message.setSubject("Test SMTP");
        message.setText("Ceci est un test");

        try {
            mailSender.send(message);
            return "Email envoyé avec succès !";
        } catch (MailException e) {
            return "Erreur d'envoi : " + e.getMessage();
        }
    }
}