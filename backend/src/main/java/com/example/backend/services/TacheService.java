package com.example.backend.services;


import com.example.backend.entities.Projet;
import com.example.backend.entities.StatutTache;
import com.example.backend.entities.Tache;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProjectRequestRepository;
import com.example.backend.repositories.TacheRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class TacheService {
    
    @Autowired
    private TacheRepository tacheRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(TacheService.class);

    public List<Tache> getAllTache() {
        return tacheRepository.findAll();
    }

    public Tache getTacheById(Long id) {
        return tacheRepository.findById(id).orElse(null);
    }

    public Tache PostTache(Tache t )
    {
        return tacheRepository.save(t);
    }

    @Transactional
    public Tache updateTache(Tache tache) {
        logger.info("Mise à jour de la tâche {} avec le statut {}", tache.getId(), tache.getStatut());
        try {
            Tache savedTache = tacheRepository.save(tache);
            logger.info("Tâche sauvegardée avec succès. Nouveau statut: {}", savedTache.getStatut());
            return savedTache;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la tâche {}", tache.getId(), e);
            throw new RuntimeException("Erreur lors de la mise à jour de la tâche", e);
        }
    }
    public void deleteTache(Long id)
    {
        tacheRepository.deleteById(id);
    }

    @Transactional
    public Tache updateStatutTache(Long id, String statut) {
        logger.info("Tentative de mise à jour du statut pour la tâche {} vers {}", id, statut);
        
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
        
        try {
            StatutTache currentStatut = tache.getStatut();
            logger.info("Statut actuel de la tâche {} : {}", id, currentStatut);
            
            StatutTache newStatut = StatutTache.valueOf(statut.toUpperCase());
            tache.setStatut(newStatut);
            
            // Forcer la sauvegarde
            Tache savedTache = tacheRepository.saveAndFlush(tache);
            tacheRepository.flush();
            
            logger.info("Tâche {} sauvegardée avec le nouveau statut : {}", id, savedTache.getStatut());
            return savedTache;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du statut de la tâche {}", id, e);
            throw new RuntimeException("Erreur lors de la mise à jour du statut", e);
        }
    }

    //kanban
    public Map<String, List<Tache>> getTachesParStatut() {
        List<Tache> taches = tacheRepository.findAll();
        Map<String, List<Tache>> groupedTaches = taches.stream()
                .collect(Collectors.groupingBy(tache -> tache.getStatut().toString()));
        return groupedTaches;
    }

    @Transactional
    public Tache forceTaskCompletion(Long id) {
        logger.info("Forçage de la tâche {} comme TERMINEE", id);
        
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
        
        try {
            // Forcer le statut à TERMINEE
            tache.setStatut(StatutTache.TERMINEE);
            
            // Sauvegarder et forcer la synchronisation
            Tache savedTache = tacheRepository.saveAndFlush(tache);
            tacheRepository.flush();
            
            logger.info("Tâche {} forcée comme TERMINEE avec succès", id);
            return savedTache;
        } catch (Exception e) {
            logger.error("Erreur lors du forçage de la tâche {} comme TERMINEE", id, e);
            throw new RuntimeException("Erreur lors du forçage du statut TERMINEE", e);
        }
    }

    @Transactional
    public Tache forceUpdateToTerminee(Long id) {
        logger.info("Forçage du statut TERMINEE pour la tâche {}", id);
        
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
        
        try {
            // Vérifier que la tâche est bien EN_COURS
            if (tache.getStatut() != StatutTache.EN_COURS) {
                throw new RuntimeException("La tâche doit être EN_COURS pour être marquée comme TERMINEE");
            }

            // Forcer le statut à TERMINEE
            tache.setStatut(StatutTache.TERMINEE);
            
            // Sauvegarder et forcer la synchronisation
            Tache savedTache = tacheRepository.saveAndFlush(tache);
            tacheRepository.flush();
            
            // Vérifier la sauvegarde
            Tache verifiedTache = tacheRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tâche non trouvée après sauvegarde"));
            
            if (verifiedTache.getStatut() != StatutTache.TERMINEE) {
                throw new RuntimeException("Le statut n'a pas été correctement mis à jour");
            }
            
            logger.info("Tâche {} forcée comme TERMINEE avec succès", id);
            return verifiedTache;
        } catch (Exception e) {
            logger.error("Erreur lors du forçage de la tâche {} comme TERMINEE", id, e);
            throw new RuntimeException("Erreur lors du forçage du statut TERMINEE", e);
        }
    }
}

