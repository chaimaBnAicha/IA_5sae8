package com.example.backend.services;

import com.example.backend.entities.Etape;
import com.example.backend.entities.Tache;
import com.example.backend.repositories.EtapeRepository;
import com.example.backend.repositories.TacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtapeService {

    @Autowired
    private EtapeRepository etapeRepository;

    @Autowired
    private TacheRepository tacheRepository;

    public Etape ajouterEtape(Long tacheId, Etape etape) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        etape.setTache(tache);
        return etapeRepository.save(etape);
    }

    public List<Etape> getEtapesByTache(Long tacheId) {
        return etapeRepository.findByTacheId(tacheId);
    }

    public List<Etape> getAllEtapes() {
        return etapeRepository.findAll();
    }

    public Etape modifierEtape(Long etapeId, Etape etapeDetails) {
        // First, verify the etape exists
        Etape existingEtape = etapeRepository.findById(etapeId)
                .orElseThrow(() -> new RuntimeException("Étape non trouvée avec l'ID : " + etapeId));

        // Update the fields with new values
        existingEtape.setNom(etapeDetails.getNom());
        existingEtape.setDescription(etapeDetails.getDescription());
        existingEtape.setDateDebut(etapeDetails.getDateDebut());
        existingEtape.setDateFin(etapeDetails.getDateFin());
        existingEtape.setStatut(etapeDetails.getStatut());

        // If tacheId is provided in etapeDetails, update the task association
        Long newTacheId = etapeDetails.getTache() != null ? etapeDetails.getTache().getId() : null;
        if (newTacheId != null) {
            Tache newTache = tacheRepository.findById(newTacheId)
                    .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + newTacheId));
            existingEtape.setTache(newTache);
        }

        try {
            // Save the updated etape and return it
            Etape updatedEtape = etapeRepository.save(existingEtape);
            etapeRepository.flush(); // Force the update to be written to the database
            return updatedEtape;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'étape : " + e.getMessage());
        }
    }

    public void supprimerEtape(Long etapeId) {
        if (!etapeRepository.existsById(etapeId)) {
            throw new RuntimeException("Étape non trouvée avec l'ID : " + etapeId);
        }
        etapeRepository.deleteById(etapeId);
    }

    public Etape getEtapeById(Long id) {
        return etapeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Etape not found with id: " + id));
    }
    public List<Etape> findByTacheId(Long tacheId) {
        return etapeRepository.findByTacheId(tacheId);
    }
}
