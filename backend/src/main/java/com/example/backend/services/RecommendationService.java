package com.example.backend.services;

import com.example.backend.entities.Request;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    public boolean isRecommended(Request request) {
        // 🔽 Logique simplifiée (tu pourras remplacer ça par un vrai modèle plus tard)
        return request.getEstimated_budget() <= 100000 &&
                Integer.parseInt(request.getEstimated_duration().replaceAll("[^0-9]", "")) <= 12;
    }

    public double getRecommendationScore(Request request) {
        // Ex : score entre 0 et 1 basé sur des critères (logique fictive ici)
        double score = 1.0;

        if (request.getEstimated_budget() > 100000) score -= 0.3;
        if (request.getEstimated_duration().toLowerCase().contains("mois") &&
                Integer.parseInt(request.getEstimated_duration().replaceAll("[^0-9]", "")) > 12) {
            score -= 0.3;
        }

        return Math.max(0, Math.min(1, score)); // Clamp entre 0 et 1
    }
}
