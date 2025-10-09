package com.example.backend.services;

import com.example.backend.entities.Offer;
import com.example.backend.entities.OfferStatus;
import com.example.backend.repositories.OfferRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j

public class OfferService implements IOfferService {
    OfferRepository offerrepo;

    @Override
    public Offer addOffer(Offer offer) {
        return offerrepo.save(offer);
    }

    @Override
    public void deleteOffer(int idOffer) {
        offerrepo.deleteById(idOffer);

    }

    @Override
    public Offer updateOffer(Offer offer) {
        return offerrepo.save(offer);
    }

    @Override
    public List<Offer> allOffers() {
        List<Offer> offers = offerrepo.findAll();
        Date currentDate = new Date();

        for (Offer offer : offers) {
            if (offer.getEnd_Date().before(currentDate) && offer.getStatus() != OfferStatus.INACTIVE) {
                offer.setStatus(OfferStatus.INACTIVE);
                offerrepo.save(offer);  // Update status if expired
            }
        }
        return offers;
    }


    @Override
    public Offer findOfferById(int idOffer) {
        return offerrepo.findById(idOffer).get();
    }
    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void deactivateExpiredOffers() {
        List<Offer> offers = offerrepo.findAll();
        Date currentDate = new Date();

        for (Offer offer : offers) {
            if (offer.getEnd_Date().before(currentDate) && offer.getStatus() != OfferStatus.INACTIVE) {
                offer.setStatus(OfferStatus.INACTIVE);
                offerrepo.save(offer);
            }
        }
    }


    // Données pour le diagramme circulaire (Statut)
    public Map<String, Long> getOfferStatusCount() {
        return offerrepo.countOffersByStatus().stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]
                ));
    }

    // Données pour le diagramme à barres (Type d'offre)
    public Map<String, Long> getTypeOfferCount() {
        return offerrepo.countOffersByType().stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]
                ));
    }

    // Données pour le graphique sinusoïdal (Offres par mois)
    public Map<String, Long> getOffersByMonth() {
        return offerrepo.countOffersByMonth().stream()
                .collect(Collectors.toMap(
                        row -> "Mois " + row[0].toString(),
                        row -> (Long) row[1]
                ));
    }





}
