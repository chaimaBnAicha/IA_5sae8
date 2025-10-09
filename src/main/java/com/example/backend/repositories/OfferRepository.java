package com.example.backend.repositories;

import com.example.backend.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository <Offer,Integer> {


    // Compter les offres par statut (Pour le diagramme circulaire)
    @Query("SELECT o.Status, COUNT(o) FROM Offer o GROUP BY o.Status")
    List<Object[]> countOffersByStatus();

    // Compter les offres par type (Pour le diagramme à barres)
    @Query("SELECT o.Typeoffer, COUNT(o) FROM Offer o GROUP BY o.Typeoffer")
    List<Object[]> countOffersByType();

    // Récupérer les dates de début pour le graphique sinusoïdal (ex. nombre d'offres par mois)
    @Query("SELECT MONTH(o.Start_Date), COUNT(o) FROM Offer o GROUP BY MONTH(o.Start_Date)")
    List<Object[]> countOffersByMonth();

}
