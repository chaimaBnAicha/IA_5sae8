package com.example.backend.controllers;

import com.example.backend.entities.Bill;
import com.example.backend.services.IBillService;
import com.example.backend.services.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/spring/payment")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private IBillService billService;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("publicKey", stripePublicKey);
        return ResponseEntity.ok(config);
    }

    /**
     * Crée un PaymentIntent pour une facture spécifique
     */
    @PostMapping("/create-payment-intent/{billId}")
    public ResponseEntity<?> createPaymentIntent(@PathVariable("billId") Integer billId) {
        try {
            // Récupérer la facture
            Bill bill = billService.getBillById(billId);

            if (bill == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Facture non trouvée avec l'ID: " + billId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // Vérifier si la facture est déjà payée
            if (bill.getPaymentMode() == Bill.PaymentMode.PAID) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Cette facture a déjà été payée");
                return ResponseEntity.badRequest().body(response);
            }

            // Créer un PaymentIntent via Stripe
            Map<String, String> paymentIntent = stripeService.createPaymentIntent(bill);

            // Ajouter la clé publique Stripe à la réponse
            paymentIntent.put("publicKey", stripePublicKey);

            return ResponseEntity.ok(paymentIntent);
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (StripeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur Stripe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Webhook pour traiter les événements Stripe
     * Note: Pour les environnements de production, vous devriez valider la signature de webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload) {
        try {
            // Dans une implémentation complète, vous auriez besoin de parser le payload
            // et vérifier la signature avec Stripe pour des raisons de sécurité

            // Exemple simplifié:
            // 1. Extraire le type d'événement et l'ID du PaymentIntent
            // 2. Si l'événement est 'payment_intent.succeeded', mettre à jour la facture

            // Pour l'exemple, nous retournons simplement un succès
            return ResponseEntity.ok("Webhook received successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }

    /**
     * Confirmer un paiement et mettre à jour le statut de la facture
     */
    @PostMapping("/confirm/{billId}/{paymentIntentId}")
    public ResponseEntity<?> confirmPayment(
            @PathVariable("billId") Integer billId,
            @PathVariable("paymentIntentId") String paymentIntentId) {
        try {
            // Récupérer la facture
            Bill bill = billService.getBillById(billId);

            // Récupérer le PaymentIntent
            PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);

            // Vérifier le statut du paiement
            if ("succeeded".equals(paymentIntent.getStatus())) {
                // Mettre à jour le statut de la facture
                bill.setPaymentMode(Bill.PaymentMode.PAID);
                Bill updatedBill = billService.updateBill(billId, bill);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Paiement confirmé avec succès");
                response.put("bill", updatedBill);

                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Le paiement n'a pas encore été réalisé");
                response.put("paymentStatus", paymentIntent.getStatus());

                return ResponseEntity.ok(response);
            }
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (StripeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur Stripe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Annuler un paiement en cours
     */
    @PostMapping("/cancel/{paymentIntentId}")
    public ResponseEntity<?> cancelPayment(@PathVariable("paymentIntentId") String paymentIntentId) {
        try {
            PaymentIntent cancelledIntent = stripeService.cancelPaymentIntent(paymentIntentId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Paiement annulé avec succès");
            response.put("status", cancelledIntent.getStatus());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur Stripe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère l'historique des paiements
     */
    @GetMapping("/history")
    public ResponseEntity<?> getPaymentHistory() {
        try {
            // Récupérer toutes les factures payées
            List<Bill> paidBills = billService.getBillsByPaymentMode(Bill.PaymentMode.PAID);

            // Transformer les factures en historique de paiements
            List<Map<String, Object>> paymentHistory = new ArrayList<>();

            for (Bill bill : paidBills) {
                Map<String, Object> payment = new HashMap<>();
                payment.put("paymentId", "pi_" + bill.getId_Bill() + "_" + Math.abs(bill.getNum_Bill().hashCode())); // Génère un ID fictif
                payment.put("billId", bill.getId_Bill());
                payment.put("billNumber", bill.getNum_Bill());
                payment.put("date", bill.getDate().toString());
                payment.put("amount", bill.getTotal_Amount());
                payment.put("status", "succeeded");
                payment.put("paymentMethod", convertStatusToPaymentMethod(bill.getStatus()));

                // Ajouter des métadonnées
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("stockId", bill.getStock() != null ? bill.getStock().getId_stock() : null);
                metadata.put("stockName", bill.getStock() != null ? bill.getStock().getName() : null);
                metadata.put("processingDate", bill.getDate().toString());

                payment.put("metadata", metadata);

                paymentHistory.add(payment);
            }

            return ResponseEntity.ok(paymentHistory);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération de l'historique des paiements: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère les détails d'un paiement spécifique
     */
    @GetMapping("/details/{paymentId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable("paymentId") String paymentId) {
        try {
            // Extraire l'ID de la facture à partir de l'ID de paiement (format: pi_BILLID_HASH)
            String[] parts = paymentId.split("_");
            if (parts.length < 2) {
                return ResponseEntity.badRequest().body(Map.of("error", "Format d'ID de paiement invalide"));
            }

            Integer billId = Integer.parseInt(parts[1]);
            Bill bill = billService.getBillById(billId);

            if (bill == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Paiement non trouvé"));
            }

            Map<String, Object> payment = new HashMap<>();
            payment.put("paymentId", paymentId);
            payment.put("billId", bill.getId_Bill());
            payment.put("billNumber", bill.getNum_Bill());
            payment.put("date", bill.getDate().toString());
            payment.put("amount", bill.getTotal_Amount());
            payment.put("status", "succeeded");
            payment.put("paymentMethod", convertStatusToPaymentMethod(bill.getStatus()));

            // Ajouter des métadonnées plus détaillées
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("stockId", bill.getStock() != null ? bill.getStock().getId_stock() : null);
            metadata.put("stockName", bill.getStock() != null ? bill.getStock().getName() : null);
            metadata.put("processingDate", bill.getDate().toString());
            metadata.put("paymentMode", bill.getPaymentMode().toString());
            metadata.put("paymentStatus", "completed");

            payment.put("metadata", metadata);

            return ResponseEntity.ok(payment);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "ID de facture invalide dans l'ID de paiement"));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des détails du paiement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Convertit le statut de la facture en méthode de paiement
     */
    private String convertStatusToPaymentMethod(Bill.Status status) {
        switch (status) {
            case CASH:
                return "Espèces";
            case TRANSFER:
                return "Virement bancaire";
            case CHECK:
                return "Chèque";
            case BANK_CARD:
                return "Carte bancaire";
            default:
                return "Autre";
        }
    }
}
