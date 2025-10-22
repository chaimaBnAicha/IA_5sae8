package com.example.backend.services;

import com.example.backend.entities.Leave;
import com.example.backend.entities.LeaveType;
import com.example.backend.models.LeaveAnalysis;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.Calendar;

@Service
public class LeaveAIService {
    
    public LeaveAnalysis analyzeLeaveRequest(Leave leave) {
        try {
            if (leave.getStart_date() == null || leave.getEnd_date() == null) {
                return new LeaveAnalysis(
                    0.0,
                    "Error: Start date and end date are required",
                    false
                );
            }

            // Calculer la durée du congé
            long duration = ChronoUnit.DAYS.between(
                leave.getStart_date().toInstant(),
                leave.getEnd_date().toInstant()
            );

            double confidenceScore = 0.85; // Score par défaut élevé
            boolean recommended = true;
            StringBuilder analysis = new StringBuilder();

            // Vérifier la durée
            if (duration < 0) {
                return new LeaveAnalysis(0.0, "Error: End date must be after start date", false);
            }

            // Analyse basée sur le type de congé
            switch (leave.getType()) {
                case Sick:
                    if (duration > 30) {
                        confidenceScore = 0.6;
                        recommended = false;
                        analysis.append("Long sick leave duration requires additional verification. ");
                    }
                    if (leave.getDocumentAttachement() == null || leave.getDocumentAttachement().isEmpty()) {
                        confidenceScore *= 0.7;
                        analysis.append("Medical documentation recommended for sick leave. ");
                    }
                    break;

                case Emergency:
                    if (duration > 7) {
                        confidenceScore = 0.7;
                        analysis.append("Extended emergency leave requires justification. ");
                    }
                    break;

                case Unpaid:
                    if (duration > 90) {
                        confidenceScore = 0.5;
                        recommended = false;
                        analysis.append("Very long unpaid leave duration - requires special approval. ");
                    }
                    break;
            }

            // Vérifier si les dates sont dans le futur
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(leave.getStart_date());
            if (calendar.before(Calendar.getInstance())) {
                confidenceScore *= 0.8;
                analysis.append("Warning: Leave start date is in the past. ");
            }

            // Analyse de la raison
            String reason = leave.getReason().toLowerCase();
            if (reason.length() < 10) {
                confidenceScore *= 0.9;
                analysis.append("Brief reason provided - more details recommended. ");
            }

            // Vérifier la durée par rapport au type
            if (duration <= 0) {
                confidenceScore = 0.0;
                recommended = false;
                analysis.append("Invalid duration: leave must be at least one day. ");
            } else if (duration == 1) {
                analysis.append("Single day leave request. ");
            } else {
                analysis.append(String.format("Multi-day leave request (%d days). ", duration));
            }

            // Ajouter une conclusion
            if (confidenceScore >= 0.8) {
                analysis.append("Request appears valid and well-documented. ");
            } else if (confidenceScore >= 0.6) {
                analysis.append("Request needs minor clarifications. ");
            } else {
                analysis.append("Request requires significant additional documentation or justification. ");
            }

            return new LeaveAnalysis(
                confidenceScore,
                analysis.toString().trim(),
                recommended
            );

        } catch (Exception e) {
            return new LeaveAnalysis(
                0.0,
                "Error analyzing leave request: " + e.getMessage(),
                false
            );
        }
    }
}