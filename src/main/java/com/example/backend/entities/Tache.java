package com.example.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(length = 500)
    private String description;

    private String details;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private Integer dureeEstimee; // en heures

    @Enumerated(EnumType.STRING)
    private StatutTache statut;

    @Enumerated(EnumType.STRING)
    private PrioriteTache priorite;

    public StatutTache getStatut() {
        return statut;
    }

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private User responsable;

    @ManyToOne
    @JoinColumn(name = "projet_id")
    private Projet projet;

    public void setStatut(StatutTache statut) {
        this.statut = statut;
    }
}
