package com.example.backend.controllers;

import com.example.backend.entities.Projet;
import com.example.backend.entities.StatutTache;
import com.example.backend.entities.Tache;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProjetRepository;
import com.example.backend.repositories.TacheRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.EmailService;
import com.example.backend.services.TacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class TacheController {

    @Autowired
    private TacheService tacheService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TacheRepository tacheRepository;
    
    @Autowired
    private UserRepository utilisateurRepository;
    
    @Autowired
    private ProjetRepository projetRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final Logger logger = LoggerFactory.getLogger(TacheController.class);

    @PostMapping("/TachePost")
    public ResponseEntity<Tache> ajouterTache(@RequestBody Tache tache) {
        logger.info("Début de l'ajout d'une nouvelle tâche");
        
        User responsable = utilisateurRepository.findById(tache.getResponsable().getId())
                .orElseThrow(() -> new RuntimeException("Responsable non trouvé"));
        logger.info("Email du responsable : " + responsable.getEmail());
        
        Projet projet = projetRepository.findById(tache.getProjet().getId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        tache.setResponsable(responsable);
        tache.setProjet(projet);
        tache.setStatut(StatutTache.valueOf("A_FAIRE"));

        Tache savedTache = tacheService.PostTache(tache);
        logger.info("Tâche sauvegardée avec l'ID : " + savedTache.getId());

        String acceptUrl = baseUrl + "/#/reponse/" + savedTache.getId() + "/oui";
        String declineUrl = baseUrl + "/#/reponse/" + savedTache.getId() + "/non";
        
        logger.info("URLs générées - Accept: {}, Decline: {}", acceptUrl, declineUrl);
        try {
            logger.info("URLs générées - Accept: {}, Decline: {}", acceptUrl, declineUrl);
            emailService.sendTaskAssignmentEmail(
                savedTache.getResponsable().getEmail(),
                savedTache,
                acceptUrl,
                declineUrl
            );
            logger.info("Email envoyé avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email", e);
        }

        return ResponseEntity.ok(savedTache);
    }

    @PutMapping("/tasks/{taskId}/respond/{response}")
    public ResponseEntity<Tache> handleTaskResponse(
        @PathVariable Long taskId,
        @PathVariable String response
    ) {
        logger.info("Réception d'une réponse pour la tâche {} : {}", taskId, response);
        try {
            Tache tache = tacheService.getTacheById(taskId);
            if (tache == null) {
                logger.error("Tâche {} non trouvée", taskId);
                return ResponseEntity.notFound().build();
            }

            switch (response.toLowerCase()) {
                case "oui":
                    logger.info("Mise à jour du statut en EN_COURS pour la tâche {}", taskId);
                    Tache updatedTache = tacheService.updateStatutTache(taskId, "EN_COURS");
                    
                    // Envoi de l'email de confirmation après acceptation
                    emailService.sendTaskAcceptanceConfirmationEmail(
                        updatedTache.getResponsable().getEmail(),
                        updatedTache
                    );
                    
                    return ResponseEntity.ok(updatedTache);
                    
                case "non":
                    logger.info("Maintien du statut A_FAIRE pour la tâche {}", taskId);
                    return ResponseEntity.ok(tache);
                    
                case "done":
                    logger.info("Marquage de la tâche comme TERMINEE");
                    return markTaskAsDone(taskId);
                    
                default:
                    logger.warn("Réponse invalide reçue : {}", response);
                    return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de la réponse pour la tâche {}", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/Taches")
    public List<Tache> getAllTache() {
        return tacheService.getAllTache();
    }

    @GetMapping("/Tache/{id}")
    public ResponseEntity<Tache> getTacheById(@PathVariable Long id)
    {
        Tache tache = tacheService.getTacheById(id);
        if (tache == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(tache);
    }

    @PutMapping("/Taches/{id}")
    public ResponseEntity<Tache> updateTache(@PathVariable Long id , @RequestBody Tache tache)
    {
        Tache existingTache = tacheService.getTacheById(id);
        if (existingTache == null)
            return ResponseEntity.notFound().build();
        existingTache.setNom(tache.getNom());
        existingTache.setDescription(tache.getDescription());
        existingTache.setDateDebut(tache.getDateDebut());
        existingTache.setDateFin(tache.getDateFin());
        existingTache.setDureeEstimee(tache.getDureeEstimee());
        existingTache.setPriorite(tache.getPriorite());
        existingTache.setStatut(tache.getStatut());
        Tache updateTache = tacheService.updateTache(existingTache);
        return ResponseEntity.ok(updateTache);
    }

    @DeleteMapping("/TacheDelete/{id}")
    public ResponseEntity<?> deleteTache(@PathVariable Long id)
    {
        Tache existingTache  = tacheService.getTacheById(id);
        if (existingTache == null)
            return ResponseEntity.notFound().build();
        tacheService.deleteTache(id);
        return ResponseEntity.ok().build();
    }

    //Kanban
    @GetMapping("/grouped")
    public Map<String, List<Tache>> getTachesParStatut() {
        return tacheService.getTachesParStatut();
    }

    @GetMapping("/taches/statut/{statut}")
    public ResponseEntity<List<Tache>> getTachesByStatut(@PathVariable String statut) {
        StatutTache statutEnum = StatutTache.valueOf(statut);
        List<Tache> taches = tacheRepository.findByStatut(statutEnum);
        return ResponseEntity.ok(taches);
    }

    @PutMapping("/api/taches/{id}/statut")
    public ResponseEntity<Tache> updateStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String newStatut = request.get("statut");
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        tache.setStatut(StatutTache.valueOf(newStatut));
        return ResponseEntity.ok(tacheRepository.save(tache));
    }

    @PutMapping("/tasks/{taskId}/done")
    @Transactional
    public ResponseEntity<Tache> markTaskAsDone(@PathVariable Long taskId) {
        logger.info("Marquage de la tâche {} comme terminée", taskId);
        try {
            // Récupérer la tâche
            Tache tache = tacheService.getTacheById(taskId);
            if (tache == null) {
                logger.error("Tâche {} non trouvée", taskId);
                return ResponseEntity.notFound().build();
            }

            // Vérifier que la tâche est bien EN_COURS
            if (tache.getStatut() != StatutTache.EN_COURS) {
                logger.error("La tâche doit être EN_COURS pour être marquée comme TERMINEE");
                return ResponseEntity.badRequest().build();
            }

            // Simuler un traitement
            logger.info("Début du traitement...");
            Thread.sleep(2000);

            // Mettre à jour le statut
            tache.setStatut(StatutTache.TERMINEE);
            Tache updatedTache = tacheRepository.saveAndFlush(tache);
            
            // Envoyer l'email
            emailService.sendTaskCompletionEmail(
                updatedTache.getResponsable().getEmail(),
                updatedTache
            );

            return ResponseEntity.ok(updatedTache);
        } catch (Exception e) {
            logger.error("Erreur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
