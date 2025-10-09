package com.example.backend.controllers;

import com.example.backend.entities.Stock;
import com.example.backend.services.IStockService;
import com.example.backend.services.EmailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spring/stock")
@CrossOrigin(origins = "http://localhost:4200")

public class StockController {

    @Autowired
    private IStockService stockService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    // Create
    @PostMapping("/add")
    public ResponseEntity<?> addStock(@Valid @RequestBody Stock stock, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        
        try {
            Stock savedStock = stockService.addStock(stock);
            return ResponseEntity.ok(savedStock);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite lors de l'ajout du stock: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Read all
    @GetMapping("/all")
    public ResponseEntity<List<Stock>> getAllStocks() {
        try {
            List<Stock> stocks = stockService.getAllStocks();
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStockById(@PathVariable("id") Integer id) {
        try {
            Stock stock = stockService.getStockById(id);
            return ResponseEntity.ok(stock);
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Update
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStock(@PathVariable("id") Integer id, 
                                       @Valid @RequestBody Stock stock, 
                                       BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Stock updatedStock = stockService.updateStock(id, stock);
            return ResponseEntity.ok(updatedStock);
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (OptimisticLockingFailureException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cette ressource a été modifiée par un autre utilisateur. Veuillez rafraîchir et réessayer.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Delete
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStock(@PathVariable("id") Integer id) {
        try {
            boolean deleted = stockService.deleteStock(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }
            Map<String, String> error = new HashMap<>();
            error.put("error", "Stock avec ID " + id + " non trouvé");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/test-email")
    public String testEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Trabelsi.Firas@esprit.tn");
        message.setTo("jamliamine413@gmail.com");
        message.setSubject("Test SMTP");
        message.setText("Ceci est un test");

        try {
            mailSender.send(message);
            return "Email envoyé avec succès !";
        } catch (MailException e) {
            e.printStackTrace();
            return "Erreur d'envoi : " + e.getMessage();
        }
    }

    @GetMapping("/stats/count-by-category")
    public ResponseEntity<?> getStockCountByCategory() {
        try {
            Map<String, Long> result = stockService.getStockCountByCategory();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/stats/value-by-category")
    public ResponseEntity<?> getTotalValueByCategory() {
        try {
            Map<String, Double> result = stockService.getTotalValueByCategory();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/stats/low-stock")
    public ResponseEntity<?> getLowStockProducts() {
        try {
            List<Stock> result = stockService.getLowStockProducts();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/stats/total-value")
    public ResponseEntity<?> getTotalStockValue() {
        try {
            Double result = stockService.getTotalStockValue();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/stats/average-price-by-category")
    public ResponseEntity<?> getAveragePriceByCategory() {
        try {
            Map<String, Double> result = stockService.getAveragePriceByCategory();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur s'est produite: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Controller is working!");
    }

    @GetMapping("/stats/test")
    public ResponseEntity<Long> testStats() {
        return ResponseEntity.ok(0L);
    }
}
