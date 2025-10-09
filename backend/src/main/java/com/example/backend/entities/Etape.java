package com.example.backend.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Etape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(length = 500)
    private String description;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    private StatutEtape statut;

    @ManyToOne
    @JoinColumn(name = "tache_id", nullable = false)
    private Tache tache;


}
