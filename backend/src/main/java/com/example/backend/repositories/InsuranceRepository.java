package com.example.backend.repositories;

import com.example.backend.entities.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InsuranceRepository extends JpaRepository<Insurance,Integer> {

    // Compter les assurances par status (ex: VALID , INVALIDE) - pour un pie chart
    /*@Query("SELECT i.status, COUNT(i) FROM Insurance i GROUP BY i.status")
    List<Object[]> countInsurancesByStatus();*/

    // Compter les assurances par catégorie (ex: RCPro, TRC...) - pour un diagramme à barres
    @Query("SELECT i.category, COUNT(i) FROM Insurance i GROUP BY i.category")
    List<Object[]> countInsurancesByCategory();

    // Compter les assurances par mois de début (ex: janvier, février...) - pour un graphique sinusoïdal
    @Query("SELECT MONTH(i.Start_Date), COUNT(i) FROM Insurance i GROUP BY MONTH(i.Start_Date)")
    List<Object[]> countInsurancesByMonth();



}
