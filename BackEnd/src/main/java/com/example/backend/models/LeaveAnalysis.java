package com.example.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveAnalysis {
    private Double confidenceScore;
    private String analysisResult;
    private Boolean recommendedApproval;
}