package com.example.backend.services;

import com.example.backend.entities.Insurance;
import com.example.backend.entities.Offer;
import com.example.backend.entities.Tache;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.base-url}")
    private String baseUrl;


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JavaMailSenderImpl mailSender1;

    public void sendVerificationEmail(String to, String verificationToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Email Verification");
        helper.setText(
                "<html><body>" +
                        "<h2>Welcome to our platform!</h2>" +
                        "<p>Please verify your email by clicking the link below:</p>" +
                        "<a href='http://localhost:4200/verify?token=" + verificationToken + "'>Verify Email</a>" +
                        "</body></html>",
                true
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String resetToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String resetLink = "http://localhost:4200/#/reset-password?token=" + resetToken;

        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText(
                "<html><body>" +
                        "<h2>Password Reset Request</h2>" +
                        "<p>Click the link below to reset your password:</p>" +
                        "<a href='" + resetLink + "' style='background-color: #4CAF50; color: white; padding: 14px 25px; text-decoration: none; display: inline-block; border-radius: 4px;'>Reset Password</a>" +
                        "<p>If you did not request this, please ignore this email.</p>" +
                        "<p>This link will expire in 24 hours.</p>" +
                        "</body></html>",
                true
        );

        mailSender.send(message);
    }
    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

        message.setContent(text, "text/plain");
        helper.setTo(to);
        helper.setSubject(subject);

        mailSender.send(message);
    }

    public void sendProjectRequestEmail(String toEmail, String projectName, double budget, String duration, String location) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Project Request Submission Confirmation");

        // ✅ URL du logo hébergé sur Imgur
        String logoUrl = "https://i.imgur.com/YX34wNO.png"; // Correction pour accéder directement à l'image

        // ✅ HTML de l'email
        String content = "<html><body>"
                + "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px;'>"
                + "<div style='text-align: center; margin-bottom: 20px;'>"
                + "<img src='" + logoUrl + "' alt='App Logo' style='max-width: 150px; margin-bottom: 10px;'>"
                + "<h2 style='color: #004085;'>Project Request Submitted</h2>"
                + "</div>"
                + "<p>Dear Customer,</p>"
                + "<p>We have successfully received your project request. Our project manager is currently reviewing it, and you will receive an update soon.</p>"
                + "<div style='background: #f8f9fa; padding: 15px; border-radius: 5px; margin-top: 10px;'>"
                + "<h3 style='color: #333;'>Project Details:</h3>"
                + "<ul>"
                + "<li><b>Project Name:</b> " + projectName + "</li>"
                + "<li><b>Estimated Budget:</b> " + budget + " €</li>"
                + "<li><b>Estimated Duration:</b> " + duration + "</li>"
                + "<li><b>Location:</b> " + location + "</li>"
                + "</ul>"
                + "</div>"
                + "<p>Thank you for your trust in our services.</p>"
                + "<p style='margin-top: 20px;'>Best regards,</p>"
                + "<p><b>Project Management Team</b></p>"
                + "</div>"
                + "</body></html>";

        helper.setText(content, true); // Activer le HTML
        mailSender.send(message);

        System.out.println("✅ Email sent successfully to: " + toEmail);
    }
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
    public void sendTaskConfirmationEmail(String to, Tache tache) {
        String subject = "Confirmation de tâche : " + tache.getNom();
        String content = buildEmailContent(tache);

        MimeMessage message = mailSender1.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender1.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur d'envoi d'email", e);
        }
    }

    private String buildEmailContent(Tache tache) {
        return "<h1>Nouvelle Tâche Assignée</h1>" +
                "<p><strong>Nom :</strong> " + tache.getNom() + "</p>" +
                "<p><strong>Description :</strong> " + tache.getDescription() + "</p>" +
                "<p>Veuillez confirmer :</p>" +
                "<a href=\"" + baseUrl + "/api/tasks/" + tache.getId() + "/accept\" " +
                "style=\"background:green; color:white; padding:10px; margin:5px;\">Oui</a>" +
                "<a href=\"" + baseUrl + "/api/tasks/" + tache.getId() + "/decline\" " +
                "style=\"background:red; color:white; padding:10px; margin:5px;\">Non</a>";
    }

    public void sendTaskAssignmentEmail(String to, Tache tache, String acceptUrl, String declineUrl) {
        logger.info("Début de l'envoi d'email - Configuration SMTP : {}:{}", mailSender1.getHost(), mailSender1.getPort());
        logger.info("Envoi à : {}", to);
        String subject = "Nouvelle tâche assignée : " + tache.getNom();

        String htmlContent = String.format("""
            <html>
            <body>
                <h2>Nouvelle tâche assignée</h2>
                <p>Vous avez été assigné à la tâche suivante :</p>
                <p><strong>Nom :</strong> %s</p>
                <p><strong>Description :</strong> %s</p>
                <p><strong>Date de début :</strong> %s</p>
                <p><strong>Date de fin :</strong> %s</p>
                <div style="margin-top: 20px;">
                    <p>Voulez-vous accepter cette tâche ?</p>
                    <a href="%s" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; margin-right: 10px;">
                        Accepter
                    </a>
                    <a href="%s" style="background-color: #f44336; color: white; padding: 10px 20px; text-decoration: none;">
                        Refuser
                    </a>
                </div>
            </body>
            </html>
            """,
                tache.getNom(),
                tache.getDescription(),
                tache.getDateDebut(),
                tache.getDateFin(),
                acceptUrl,
                declineUrl
        );

        try {
            logger.info("Création du message email");
            MimeMessage message = mailSender1.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            logger.info("Tentative d'envoi de l'email...");
            mailSender1.send(message);
            logger.info("Email envoyé avec succès");
        } catch (MessagingException e) {
            logger.error("Erreur détaillée lors de l'envoi de l'email", e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage(), e);
        }
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender1.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender1.send(message);
            logger.info("Email envoyé avec succès à {}", to);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email à {}", to, e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage(), e);
        }
    }

    public void sendTaskAcceptanceConfirmationEmail(String to, Tache tache) {
        logger.info("Envoi de l'email de confirmation d'acceptation à {}", to);
        String subject = "Confirmation d'acceptation de la tâche";
        String completeUrl = baseUrl + "/#/reponse/" + tache.getId() + "/done";

        String htmlContent = String.format("""
            <html>
            <body>
                <h2>Tâche acceptée avec succès</h2>
                <p>Vous avez accepté la tâche suivante :</p>
                <p><strong>Nom :</strong> %s</p>
                <p><strong>Description :</strong> %s</p>
                <p>Une fois la tâche terminée, cliquez sur le lien ci-dessous :</p>
                <p><a href="%s">Marquer comme terminée</a></p>
            </body>
            </html>
            """,
                tache.getNom(),
                tache.getDescription(),
                completeUrl
        );

        sendEmail(to, subject, htmlContent);
    }

    public void sendTaskCompletionEmail(String to, Tache tache) {
        logger.info("Envoi de l'email de confirmation de fin de tâche à {}", to);
        String subject = "Tâche terminée : " + tache.getNom();

        String htmlContent = String.format("""
            <html>
            <body>
                <h2>Tâche marquée comme terminée</h2>
                <p>La tâche suivante a été marquée comme terminée :</p>
                <p><strong>Nom :</strong> %s</p>
                <p><strong>Description :</strong> %s</p>
                <p><strong>Date de fin effective :</strong> %s</p>
                <p style="color: green;"><strong>Statut :</strong> TERMINEE</p>
            </body>
            </html>
            """,
                tache.getNom(),
                tache.getDescription(),
                tache.getDateFin()
        );

        try {
            MimeMessage message = mailSender1.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender1.send(message);
            logger.info("Email de confirmation de fin de tâche envoyé avec succès");
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation de fin de tâche", e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de confirmation: " + e.getMessage(), e);
        }
    }
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail1(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("benaichachaima88@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
    public void sendHtmlEmail1(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
    public void sendStockNotification(String stockName, String recipientEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Trabelsi.Firas@gmail.com");
        message.setTo(recipientEmail);
        message.setSubject("Nouveau stock ajouté");
        message.setText("Un nouvel élément de stock '" + stockName + "' a été ajouté avec succès.");

        mailSender.send(message);
    }

    // Méthode de test
    public boolean testEmailConnection() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Trabelsi.Firas@gmail.com");
            message.setTo("jamliamine413@gmail.com");
            message.setSubject("Test Email");
            message.setText("Ceci est un email de test pour vérifier la configuration.");

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            // Affichage détaillé de l'erreur
            System.err.println("Erreur détaillée lors de l'envoi d'email: ");
            e.printStackTrace();

            // Affichage de la cause racine
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            System.err.println("Cause racine: " + rootCause.getMessage());

            return false;
        }
    }
    public void sendOfferNotification(String userEmail, Offer offer) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(userEmail);
        helper.setSubject("🎉 New Offer Available!");

        String emailContent = buildEmailContent(offer);

        helper.setText(emailContent, true); // Enable HTML content
        mailSender.send(message);
    }

    private String buildEmailContent(Offer offer) {
        // Convert java.util.Date to LocalDate
        LocalDate startDate = offer.getStart_Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = offer.getEnd_Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Format the LocalDate to dd/MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        return "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 8px; padding: 20px; background-color: #f9f9f9;'>"
                + "    <div style='text-align: center; margin-bottom: 20px;'>"
                + "        <img src='https://i.imgur.com/YX34wNO.png' alt='Builderz Logo' style='max-width: 150px;'>"
                + "    </div>"
                + "    <h2 style='color: #16A085; text-align: center; font-size: 28px;'>🚀 New Offer Available!</h2>"
                + "    <p style='font-size: 18px; color: #555; text-align: center;'>A new offer has been added. Check out the details below:</p>"
                + "    <div style='background: #fff; padding: 15px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);'>"
                + "        <p style='font-size: 18px;'><strong>📌 Title:</strong> " + offer.getTitle() + "</p>"
                + "        <p style='font-size: 18px;'><strong>📝 Description:</strong> " + offer.getDescription() + "</p>"
                + "        <p style='font-size: 18px;'><strong>📅 Valid From:</strong> " + formattedStartDate + " to " + formattedEndDate + "</p>"
                + "    </div>"
                + "    <div style='text-align: center; margin-top: 20px;'>"
                + "        <a href='http://localhost:4200/#/app-view-offers" + offer.getId_offer() + "'"
                + "           style='display: inline-block; padding: 12px 20px; background-color: #16A085; color: #fff; font-size: 18px; font-weight: bold; text-decoration: none; border-radius: 5px; box-shadow: 0 4px 6px rgba(0, 123, 255, 0.3);'>"
                + "            🔗 View Offer"
                + "        </a>"
                + "    </div>"
                + "    <p style='text-align: center; color: #777; font-size: 16px; margin-top: 20px;'>Thank you for using our platform!</p>"
                + "    <hr style='border: 0; height: 1px; background: #ddd; margin: 20px 0;'>"
                + "    <p style='text-align: center; color: #555; font-size: 16px;'>Best regards,<br><strong>Builderz Team</strong></p>"
                + "</div>";
    }






    public void sendInsuranceNotification(String userEmail, Insurance insurance) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(userEmail);
        helper.setSubject("🎉 New Insurance Available!");

        String emailContent = buildEmailContent(insurance);

        helper.setText(emailContent, true); // Enable HTML content
        mailSender.send(message);
    }

    private String buildEmailContent(Insurance insurance) {
        // Convert java.util.Date to LocalDate
        LocalDate startDate = insurance.getStart_Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = insurance.getEnd_Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Format the LocalDate to dd/MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        return "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 8px; padding: 20px; background-color: #f9f9f9;'>"
                + "    <div style='text-align: center; margin-bottom: 20px;'>"
                + "        <img src='https://i.imgur.com/YX34wNO.png' alt='Builderz Logo' style='max-width: 150px;'>"
                + "    </div>"
                + "    <h2 style='color: #16A085; text-align: center; font-size: 28px;'>🚀 New Insurance Available!</h2>"
                + "    <p style='font-size: 18px; color: #555; text-align: center;'>A new offer has been added. Check out the details below:</p>"
                + "    <div style='background: #fff; padding: 15px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);'>"

                + "        <p style='font-size: 18px;'><strong>📝 Description:</strong> " + insurance.getDescription() + "</p>"
                + "        <p style='font-size: 18px;'><strong>📅 Valid From:</strong> " + formattedStartDate + " to " + formattedEndDate + "</p>"
                + "    </div>"
                + "    <div style='text-align: center; margin-top: 20px;'>"
                + "        <a href='http://localhost:4200/#/app-view-offers" + insurance.getId_Insurance() + "'"
                + "           style='display: inline-block; padding: 12px 20px; background-color: #16A085; color: #fff; font-size: 18px; font-weight: bold; text-decoration: none; border-radius: 5px; box-shadow: 0 4px 6px rgba(0, 123, 255, 0.3);'>"
                + "            🔗 View Insurance"
                + "        </a>"
                + "    </div>"
                + "    <p style='text-align: center; color: #777; font-size: 16px; margin-top: 20px;'>Thank you for using our platform!</p>"
                + "    <hr style='border: 0; height: 1px; background: #ddd; margin: 20px 0;'>"
                + "    <p style='text-align: center; color: #555; font-size: 16px;'>Best regards,<br><strong>Builderz Team</strong></p>"
                + "</div>";
    }



}
