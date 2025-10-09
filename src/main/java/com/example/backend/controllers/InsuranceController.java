package com.example.backend.controllers;


import com.example.backend.entities.*;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.EmailService;
import com.example.backend.services.IServiceInsurance;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/Insurance")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})



public class InsuranceController {



   IServiceInsurance InsuranceService;
    EmailService emailService;
    UserRepository userRepository;
    @GetMapping("/retrieve-all-Insurances")
    public List<Insurance> getInsurances() {
        List<Insurance> listInsurances = InsuranceService.allInsurances();
        return listInsurances;
    }

    @GetMapping("/retrieve-Insurance/{Insurance-id}")
    public Insurance retrieveInsurance(@PathVariable("Insurance-id") int Id) {
        Insurance Insurance = InsuranceService.findInsuranceById(Id);
        return Insurance;
    }


  /*  @PostMapping("/add-Insurance")
    public Insurance addInsurance(@RequestBody Insurance a) {
        Insurance Insurance = InsuranceService.addInsurance(a);

        Insurance savedInsurance = InsuranceService.addInsurance(a);
        try {
            String userEmail = savedInsurance.getUser().getEmail();
            emailService.sendInsuranceNotification(userEmail, savedInsurance);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
        return Insurance;
    }*/
  @PostMapping("/add-Insurance")
  public Insurance addInsurance(@RequestBody Insurance a) {
      // Force the user ID to 2 regardless of what comes in the request
      User user = userRepository.findById(3L)
              .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : 3"));

      // Set the user to the one with ID 2
      a.setUser(user);

      Insurance savedInsurance = InsuranceService.addInsurance(a);

      try {
          if (user.getEmail() == null) {
              System.err.println("L'email de l'utilisateur est null");
          } else {
              String userEmail = user.getEmail();
              System.out.println("Email à envoyer : " + userEmail);
              emailService.sendInsuranceNotification(userEmail, savedInsurance);
          }
      } catch (Exception e) {
          System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
      }

      return savedInsurance;
  }

    @DeleteMapping("/remove-Insurance/{Insurance-id}")
    public void removeInsurance(@PathVariable("Insurance-id") int Id) {
        InsuranceService.deleteInsurance(Id);
    }
    @PutMapping("/modify-Insurance")
    public Insurance modifyInsurance(@RequestBody Insurance a) {
        Insurance Insurance = InsuranceService.updateInsurance(a);
        return Insurance;
    }



    // 📊 Endpoint pour compter les assurances par statut (VALID / EXPIRED)
   /* @GetMapping("/status-count")
    public Map<InsuranceStatus, Long> getInsuranceStatusCount() {
        return InsuranceService.countInsurancesByStatus();
    }*/

    @GetMapping("/status-count")
    public Map<InsuranceStatus, Long> getInsuranceStatusCount() {
        return InsuranceService.countInsurancesByStatus();
    }



    @GetMapping("/category-count")
    public ResponseEntity<Map<Category, Long>> countInsurancesByCategory() {
        try {
            Map<Category, Long> categoryCount = InsuranceService.countInsurancesByCategory();
            return ResponseEntity.ok(categoryCount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 📈 Mois de début (1 à 12)
    @GetMapping("/monthly-count")
    public Map<Integer, Long> getInsuranceMonthlyCount() {
        return InsuranceService.countInsurancesByMonth();
    }
    
     /* @GetMapping("/category-count")
    public ResponseEntity<Map<Category, Long>> countInsurancesByCategory() {
        return ResponseEntity.ok(InsuranceService.countInsurancesByCategory());
    }*/

}
