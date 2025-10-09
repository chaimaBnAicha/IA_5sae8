package com.example.backend.services;

import com.example.backend.entities.Bill;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Crée un PaymentIntent pour une facture donnée
     * @param bill La facture à payer
     * @return Un objet contenant le client secret et l'ID du PaymentIntent
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public Map<String, String> createPaymentIntent(Bill bill) throws StripeException {
        // Calcule le montant en centimes (Stripe utilise les centimes)
        long amount = Math.round(bill.getTotal_Amount() * 100);

        // Garantir un montant minimum de 50 centimes (requis par Stripe)
        if (amount < 50) {
            amount = 50;
        }

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setCurrency("eur")
                        .setAmount(amount)
                        .setDescription("Paiement de la facture #" + bill.getNum_Bill())
                        .putMetadata("bill_id", bill.getId_Bill().toString())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());

        return response;
    }

    /**
     * Récupère un PaymentIntent existant
     * @param paymentIntentId L'ID du PaymentIntent à récupérer
     * @return L'objet PaymentIntent
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    /**
     * Annule un PaymentIntent existant
     * @param paymentIntentId L'ID du PaymentIntent à annuler
     * @return L'objet PaymentIntent annulé
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntent cancelledPaymentIntent = paymentIntent.cancel();
        return cancelledPaymentIntent;
    }
}
