package com.example.backend.DTOs;
public class RequestDTO {
    private Long id_projet;
    private String projectName;
    private String description;
    private Double estimated_budget;
    private String estimated_duration;
    private String geographic_location;
    private Double recommendationScore;
    private String analysisResult;
    private String status;
    private String priority;
    private Long user_id;

    public Long getId_projet() {
        return id_projet;
    }

    public void setId_projet(Long id_projet) {
        this.id_projet = id_projet;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getEstimated_budget() {
        return estimated_budget;
    }

    public void setEstimated_budget(Double estimated_budget) {
        this.estimated_budget = estimated_budget;
    }

    public String getEstimated_duration() {
        return estimated_duration;
    }

    public void setEstimated_duration(String estimated_duration) {
        this.estimated_duration = estimated_duration;
    }

    public String getGeographic_location() {
        return geographic_location;
    }

    public void setGeographic_location(String geographic_location) {
        this.geographic_location = geographic_location;
    }

    public Double getRecommendationScore() {
        return recommendationScore;
    }

    public void setRecommendationScore(Double recommendationScore) {
        this.recommendationScore = recommendationScore;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}