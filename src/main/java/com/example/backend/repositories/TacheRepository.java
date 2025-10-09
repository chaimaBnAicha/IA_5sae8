package com.example.backend.repositories;

import com.example.backend.entities.StatutTache;
import com.example.backend.entities.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache,Long> {

        List<Tache> findByStatut(StatutTache statut);
    }

