package com.example.backend.controllers;

import com.example.backend.entities.Leave;
import com.example.backend.entities.LeaveStatus;
import com.example.backend.entities.LeaveType;
import com.example.backend.entities.User;
import com.example.backend.models.LeaveAnalysis;
import com.example.backend.services.EmailService;
import com.example.backend.services.ILeaveService;
import com.example.backend.services.LeaveAIService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/leave")
@CrossOrigin(origins = "http://localhost:4200",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*")
public class LeaveController {

    ILeaveService leaveService;
    private final EmailService emailService;
    private final LeaveAIService leaveAIService;

    @GetMapping("/retrieve-all-leave")
    public List<Leave> getLeaves() {
        List<Leave> listLeaves = leaveService.allLeaves();
        return listLeaves;
    }


    @PostMapping("/add-leave")
    public ResponseEntity<?> addLeave(@RequestBody Leave l) {
        // Analyser la demande avec l'IA
        LeaveAnalysis analysis = leaveAIService.analyzeLeaveRequest(l);
        
        // Mettre à jour la demande avec les résultats de l'analyse
        l.setAiConfidenceScore(analysis.getConfidenceScore());
        l.setAiAnalysisResult(analysis.getAnalysisResult());
        l.setAiRecommendedApproval(analysis.getRecommendedApproval());
        
        // Si le score de confiance est trop bas, nous pourrions rejeter automatiquement
        if (analysis.getConfidenceScore() < 0.3) {
            l.setStatus(LeaveStatus.Rejected);
        }
        
        // Sauvegarder la demande
        Leave leave = leaveService.addLeave(l);
        
        // Retourner la demande et l'analyse
        return ResponseEntity.ok(Map.of(
            "leave", leave,
            "analysis", analysis
        ));
    }
    @DeleteMapping("/remove-leave/{leave-id}")
    public void removeLeave(@PathVariable("leave-id") int Id) {
        leaveService.deleteLeave(Id);
    }

    @PutMapping("/modify-Leave")
    public Leave modifyLeave(@RequestBody Leave l) {
        Leave leave = leaveService.updateLeave(l);
        return leave;
    }
    @GetMapping("/retrieve-leave/{leave-id}")
    public Leave retrieveLeave(@PathVariable("leave-id") int Id) {
        Leave leave = leaveService.findLeaveById(Id);
        return leave;
    }
    @GetMapping("/can-accept")
    public boolean canAcceptLeave(
            @RequestParam int userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam LeaveType type,
            @RequestParam(required = false) String document,
            @RequestParam LeaveStatus status
    ) {
        return leaveService.canAcceptLeave(userId, startDate, endDate, type, document, status);
    }
    @PostMapping("/send")
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam(defaultValue = "false") boolean isHtml) {

        if (isHtml) {
            emailService.sendHtmlEmail(to, subject, body);
        } else {
            emailService.sendEmail(to, subject, body);
        }

        return "Email sent successfully!";
    }

    @GetMapping("/leave-count-by-type")
    public Map<String, Long> getLeaveCountByType() {
        return leaveService.getLeaveCountByType();
    }

    @GetMapping("/average-leave-duration")
    public Map<String, Double> getAverageLeaveDurationPerType() {
        return leaveService.getAverageLeaveDurationPerType();
    }

    @GetMapping("/leaves-by-month")
    public Map<Integer, Long> getApprovedLeavesByMonth() {
        return leaveService.getApprovedLeavesByMonth();
    }

    @PostMapping("/analyze-leave")
    public ResponseEntity<LeaveAnalysis> analyzeLeave(@RequestBody Leave leave) {
        try {
            // Set default values if not provided
            if (leave.getStatus() == null) {
                leave.setStatus(LeaveStatus.Pending);
            }
            
            // Initialize minimal user data if not provided
            if (leave.getUser() == null) {
                User user = new User();
                user.setId(1);
                leave.setUser(user);
            }
            
            // Vérification des champs requis
            if (leave.getStart_date() == null || leave.getEnd_date() == null) {
                return ResponseEntity.badRequest().body(new LeaveAnalysis(
                    0.0,
                    "Start date and end date are required",
                    false
                ));
            }
            
            if (leave.getReason() == null || leave.getReason().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new LeaveAnalysis(
                    0.0,
                    "Reason is required",
                    false
                ));
            }
            
            if (leave.getType() == null) {
                return ResponseEntity.badRequest().body(new LeaveAnalysis(
                    0.0,
                    "Leave type is required",
                    false
                ));
            }
            
            LeaveAnalysis analysis = leaveAIService.analyzeLeaveRequest(leave);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LeaveAnalysis(
                0.0,
                "Error processing request: " + e.getMessage(),
                false
            ));
        }
    }

    @PutMapping("/update-leave-with-ai")
    public ResponseEntity<?> updateLeaveWithAI(@PathVariable int id) {
        Leave leave = leaveService.findLeaveById(id);
        if (leave == null) {
            return ResponseEntity.notFound().build();
        }

        LeaveAnalysis analysis = leaveAIService.analyzeLeaveRequest(leave);
        
        leave.setAiConfidenceScore(analysis.getConfidenceScore());
        leave.setAiAnalysisResult(analysis.getAnalysisResult());
        leave.setAiRecommendedApproval(analysis.getRecommendedApproval());
        
        leave = leaveService.updateLeave(leave);
        
        return ResponseEntity.ok(Map.of(
            "leave", leave,
            "analysis", analysis
        ));
    }
}
