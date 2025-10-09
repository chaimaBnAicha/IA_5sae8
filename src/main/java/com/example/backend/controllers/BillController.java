package com.example.backend.controllers;

import com.example.backend.entities.Bill;
import com.example.backend.entities.Stock;
import com.example.backend.repositories.StockRepository;
import com.example.backend.services.IBillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/spring/bill")
@CrossOrigin(origins = "http://localhost:4200")
public class BillController {
    
    @Autowired
    private IBillService billService;
    
    @Autowired
    private StockRepository stockRepository;
    
    // Create
    @PostMapping("/add")
    public ResponseEntity<?> addBill(@Valid @RequestBody Bill bill) {
        try {
            // Vérifier si le stock existe
            if (bill.getStock() != null && bill.getStock().getId_stock() != null) {
                Optional<Stock> stockOpt = stockRepository.findById(bill.getStock().getId_stock());
                if (!stockOpt.isPresent()) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Le stock avec l'ID " + bill.getStock().getId_stock() + " n'existe pas");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                }
            }
            
            Bill savedBill = billService.addBill(bill);
            return ResponseEntity.ok(savedBill);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            
            // Ajouter des informations sur les valeurs acceptables
            Map<String, String[]> validValues = new HashMap<>();
            validValues.put("status", getEnumValuesAsStrings(Bill.Status.class));
            validValues.put("paymentMode", getEnumValuesAsStrings(Bill.PaymentMode.class));
            error.put("validValues", validValues);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la création de la facture: " + e.getMessage());
            e.printStackTrace(); // Pour faciliter le débogage
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Read all
    @GetMapping("/all")
    public List<Bill> getAllBills() {
        return billService.getAllBills();
    }
    
    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable("id") Integer id) {
        Bill bill = billService.getBillById(id);
        if (bill != null) {
            return ResponseEntity.ok(bill);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Update
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBill(@PathVariable("id") Integer id, 
                                          @Valid @RequestBody Bill bill) {
        try {
            // Vérifier si le stock existe
            if (bill.getStock() != null && bill.getStock().getId_stock() != null) {
                Optional<Stock> stockOpt = stockRepository.findById(bill.getStock().getId_stock());
                if (!stockOpt.isPresent()) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Le stock avec l'ID " + bill.getStock().getId_stock() + " n'existe pas");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                }
            }
            
            Bill updatedBill = billService.updateBill(id, bill);
            if (updatedBill != null) {
                return ResponseEntity.ok(updatedBill);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la mise à jour de la facture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Delete
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable("id") Integer id) {
        boolean deleted = billService.deleteBill(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getValidValues() {
        Map<String, Object> info = new HashMap<>();
        
        // Fournir des informations sur les valeurs valides pour les énumérations
        Map<String, String[]> validValues = new HashMap<>();
        validValues.put("status", getEnumValuesAsStrings(Bill.Status.class));
        validValues.put("paymentMode", getEnumValuesAsStrings(Bill.PaymentMode.class));
        
        info.put("validValues", validValues);
        info.put("example", getExampleBill());
        
        return ResponseEntity.ok(info);
    }
    
    // Méthode utilitaire pour obtenir toutes les valeurs d'une énumération
    private <E extends Enum<E>> String[] getEnumValuesAsStrings(Class<E> enumClass) {
        return java.util.Arrays.stream(enumClass.getEnumConstants())
                              .map(Enum::name)
                              .toArray(String[]::new);
    }
    
    private Map<String, Object> getExampleBill() {
        Map<String, Object> example = new HashMap<>();
        example.put("num_Bill", "BILL-2023-001");
        example.put("date", "2023-11-20");
        example.put("total_Amount", 1250.50);
        example.put("status", "CASH");
        example.put("paymentMode", "PAID");
        
        Map<String, Object> stock = new HashMap<>();
        stock.put("id_stock", 1);
        stock.put("name", "Exemple de Stock"); // Ajout du nom pour l'exemple
        example.put("stock", stock);
        
        return example;
    }
    
    @GetMapping("/stock-name/{billId}")
    public ResponseEntity<?> getStockNameForBill(@PathVariable("billId") Integer billId) {
        try {
            Bill bill = billService.getBillById(billId);
            if (bill == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Facture avec ID " + billId + " non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            Stock stock = bill.getStock();
            if (stock == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Aucun stock associé à cette facture");
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("billId", bill.getId_Bill());
            response.put("billNumber", bill.getNum_Bill());
            response.put("stockId", stock.getId_stock());
            response.put("stockName", stock.getName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des informations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Récupérer une facture avec son URL de paiement
     */
    @GetMapping("/payment-info/{billId}")
    public ResponseEntity<?> getBillWithPaymentURL(@PathVariable("billId") Integer billId) {
        try {
            Bill bill = billService.getBillById(billId);
            if (bill == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Facture avec ID " + billId + " non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            // Si la facture est déjà payée, pas besoin d'URL de paiement
            if (bill.getPaymentMode() == Bill.PaymentMode.PAID) {
                Map<String, Object> response = new HashMap<>();
                response.put("bill", bill);
                response.put("message", "Cette facture a déjà été payée");
                return ResponseEntity.ok(response);
            }
            
            // Générer URL de paiement (dans une application réelle, cette URL serait générée par le frontend)
            String paymentURL = "/payment/" + billId;
            
            Map<String, Object> response = new HashMap<>();
            response.put("bill", bill);
            response.put("paymentURL", paymentURL);
            response.put("canPay", true);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des informations de paiement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Guide de test avec Postman pour l'intégration Stripe:
     * 
     * 1. Test de la configuration Stripe:
     *    - GET http://localhost:8081/spring/payment/config
     *    - Cette requête retourne la clé publique Stripe pour l'intégration frontend
     * 
     * 2. Création d'un PaymentIntent pour une facture:
     *    - POST http://localhost:8081/spring/payment/create-payment-intent/1
     *      (remplacez "1" par l'ID d'une facture existante)
     *    - Vous recevrez un client_secret à utiliser côté frontend
     * 
     * 3. Simulation d'un paiement confirmé:
     *    - POST http://localhost:8081/spring/payment/confirm/1/pi_12345
     *      (remplacez "1" par l'ID de la facture et "pi_12345" par l'ID du PaymentIntent)
     *    - Cette requête simule un paiement réussi et met à jour le statut de la facture
     * 
     * 4. Annulation d'un paiement:
     *    - POST http://localhost:8081/spring/payment/cancel/pi_12345
     *      (remplacez "pi_12345" par l'ID du PaymentIntent à annuler)
     * 
     * 5. Vérification des informations de paiement d'une facture:
     *    - GET http://localhost:8081/spring/bill/payment-info/1
     *      (remplacez "1" par l'ID de la facture)
     *    - Vous recevrez les détails de la facture et son statut de paiement
     */
    
    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Bill Controller is working!");
    }
}
