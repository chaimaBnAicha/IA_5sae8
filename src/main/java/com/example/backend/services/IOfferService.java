package com.example.backend.services;

import com.example.backend.entities.Offer;

import java.util.List;
import java.util.Map;

public interface IOfferService {


    Offer addOffer(Offer offer);
    void deleteOffer(int idOffer);
    Offer updateOffer(Offer offer);
    List<Offer> allOffers();
    Offer findOfferById(int idOffer);

    Map<String, Long> getOffersByMonth();
    Map<String, Long> getTypeOfferCount();
    Map<String, Long> getOfferStatusCount();
    
}
