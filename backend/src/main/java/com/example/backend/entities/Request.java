package com.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_projet;

    @NotBlank(message = "Le nom du projet est obligatoire")
    private String projectName;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Positive(message = "Le budget doit être un nombre positif")
    private double estimated_budget;

    @NotBlank(message = "La durée estimée est obligatoire")
    private String estimated_duration;

    @NotBlank(message = "La localisation est obligatoire")
    private String geographic_location;
    @Column(name = "recommendation_score")
    private Double recommendationScore; // null = pas encore évalué
    @Column(columnDefinition = "TEXT")
    private String analysisResult;




    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    // 🔹 Statut de la demande
    @Enumerated(EnumType.STRING)
    private Statut status = Statut.Pending; // Par défaut "Pending"

    // 🔹 Priorité de la demande
    @Enumerated(EnumType.STRING)
    private PriorityLevel priority = PriorityLevel.MEDIUM; // Par défaut "Moyenne"
}
