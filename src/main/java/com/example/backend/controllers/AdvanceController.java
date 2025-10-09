package com.example.backend.controllers;

import com.example.backend.entities.Advance;
import com.example.backend.services.EmailService;
import com.example.backend.services.IAdvanceService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/advance")
@CrossOrigin(origins = "http://localhost:4200")
public class AdvanceController {

    IAdvanceService advanceService;
    private final EmailService emailService;
    @GetMapping("/retrieve-all-advances")
    public List<Advance> getAdvances() {
        List<Advance> listAdvances = advanceService.allAdvances();
        return listAdvances;
    }

    @GetMapping("/retrieve-advance/{advance-id}")
    public Advance retrieveAdvance(@PathVariable("advance-id") int Id) {
        Advance advance = advanceService.findAdvanceById(Id);
        return advance;
    }

    @PostMapping("/add-advance")
    public Advance addAdvance(@RequestBody Advance a) {
        Advance advance = advanceService.addAdvance(a);
        return advance;
    }
    @DeleteMapping("/remove-advance/{advance-id}")
    public void removeAdvance(@PathVariable("advance-id") int Id) {
        advanceService.deleteAdvance(Id);
    }
    @PutMapping("/modify-advance")
    public Advance modifyAdvance(@RequestBody Advance a) {
        Advance advance = advanceService.updateAdvance(a);
        return advance;
    }
    @GetMapping("/user/{userId}")
    public List<Advance> getAdvancesByUserId(@PathVariable int userId) {
        return advanceService.getAllAdvancesByUser(userId);
    }

    @GetMapping("/can-approve")
    public ResponseEntity<?> canApproveAdvance(
            @RequestParam int userId,
            @RequestParam int advanceId) {
        try {
            boolean result = advanceService.canApproveAdvance(userId, advanceId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking approval status: " + e.getMessage());
        }
    }
    /*@PutMapping("/update-status/{id}")
    public Advance updateAdvanceStatus(@PathVariable("id") int id, @RequestBody String status) {
        return advanceService.updateAdvanceStatus(id, status);
    }*/

  /*  @PostMapping("/notify-status")
    public ResponseEntity<?> notifyUser(@RequestBody Map<String, Object> request) {
        try {
            int advanceId = (int) request.get("advanceId");
            String status = (String) request.get("status");

            // Get the advance details
            Advance advance = advanceService.findAdvanceById(advanceId);

            // Create and send email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo("Syrinezaier283@gmail.com");  // Set the recipient email
            mailMessage.setSubject("Advance Request Status Update");
            mailMessage.setText("Your advance request for " + advance.getAmount_request() +
                    " has been " + status.toLowerCase());

            mailSender.send(mailMessage);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send notification: " + e.getMessage());
        }
    }*/

    @PostMapping("/send")
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam(defaultValue = "false") boolean isHtml) {

        if (isHtml) {
            emailService.sendHtmlEmail1(to, subject, body);
        } else {
            emailService.sendEmail1(to, subject, body);
        }

        return "Email sent successfully!";
    }
    @GetMapping("/status")
    public Map<String, Long> getAdvancesByStatus() {
        return advanceService.getAdvancesByStatus();
    }

    @GetMapping("/monthly")
    public Map<String, Long> getAdvancesByMonth() {
        return advanceService.getAdvancesByMonth();
    }

    @GetMapping("/sinusoidal")
    public Map<String, Double> getSinusoidalData() {
        return advanceService.getSinusoidalData();
    }


}
