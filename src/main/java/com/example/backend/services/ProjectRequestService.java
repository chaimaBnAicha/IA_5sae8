package com.example.backend.services;

import com.example.backend.entities.Request;
import com.example.backend.entities.Statut;
import com.example.backend.repositories.ProjectRequestRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectRequestService {

    @Autowired
    private ProjectRequestRepository projectRequestRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private RecommendationService recommendationService;

    public List<Request> getAllRequests() {
        return projectRequestRepository.findAll();
    }

    public Request getRequestById(Long id_projet) {
        return projectRequestRepository.findById(id_projet).orElse(null);
    }
    public List<Request> getRequestsByStatus(Statut status) {
        return projectRequestRepository.findByStatus(status);
    }

    public Request createProjectRequest(Request projectRequest) {
        double score = recommendationService.getRecommendationScore(projectRequest);
        projectRequest.setRecommendationScore(score);
        Request savedRequest = projectRequestRepository.save(projectRequest);

        // Récupérer l'email du client
        String clientEmail = savedRequest.getUser().getEmail();

        // Envoyer l'email de confirmation
        try {
            emailService.sendProjectRequestEmail(
                    clientEmail,
                    savedRequest.getProjectName(),
                    savedRequest.getEstimated_budget(),
                    savedRequest.getEstimated_duration(),
                    savedRequest.getGeographic_location()
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return savedRequest;
    }

    public Request updateRequest(Request request) {
        return projectRequestRepository.save(request);
    }

    public void deleteRequest(Long id_project) {
        projectRequestRepository.deleteById(id_project);
    }
    public List<Request> searchRequests(String query) {
        return projectRequestRepository.findByProjectNameContainingIgnoreCase(query);
    }
    public Request approveRequest(Long id) {
        Optional<Request> request = projectRequestRepository.findById(id);
        if (request.isPresent()) {
            Request approvedRequest = request.get();
            approvedRequest.setStatus(Statut.Approved);
            Request savedRequest = projectRequestRepository.save(approvedRequest);

            // ✅ Envoi de l'e-mail
            String clientEmail = approvedRequest.getUser().getEmail();
            String logoUrl = "https://i.imgur.com/YX34wNO.png";

            String content = "<html><body>"
                    + "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #cce5ff; padding: 20px; border-radius: 8px;'>"
                    + "<div style='text-align: center; margin-bottom: 20px;'>"
                    + "<img src='" + logoUrl + "' alt='App Logo' style='max-width: 150px;'>"
                    + "<h2 style='color: #155724;'>Your Project Request has been <span style='color: green;'>Approved</span></h2>"
                    + "</div>"
                    + "<p>Dear Customer,</p>"
                    + "<p>We are pleased to inform you that your project request has been <strong>approved</strong> by our manager.</p>"
                    + "<p>We will contact you soon for the next steps.</p>"
                    + "<p style='margin-top: 20px;'>Best regards,<br><b>Project Management Team</b></p>"
                    + "</div></body></html>";

            try {
                emailService.sendHtmlEmail(clientEmail, "Project Request Approved", content);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            return savedRequest;
        }
        return null;
    }


    public Request rejectRequest(Long id) {
        Optional<Request> request = projectRequestRepository.findById(id);
        if (request.isPresent()) {
            Request rejectedRequest = request.get();
            rejectedRequest.setStatus(Statut.Rejected);
            Request savedRequest = projectRequestRepository.save(rejectedRequest);

            // ✅ Envoi de l'e-mail
            String clientEmail = rejectedRequest.getUser().getEmail();
            String logoUrl = "https://i.imgur.com/YX34wNO.png";

            String content = "<html><body>"
                    + "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #f5c6cb; padding: 20px; border-radius: 8px;'>"
                    + "<div style='text-align: center; margin-bottom: 20px;'>"
                    + "<img src='" + logoUrl + "' alt='App Logo' style='max-width: 150px;'>"
                    + "<h2 style='color: #721c24;'>Your Project Request has been <span style='color: red;'>Rejected</span></h2>"
                    + "</div>"
                    + "<p>Dear Customer,</p>"
                    + "<p>We regret to inform you that your project request has been <strong>rejected</strong> after careful consideration.</p>"
                    + "<p>If you have any questions or wish to modify your request, please feel free to contact us.</p>"
                    + "<p style='margin-top: 20px;'>Sincerely,<br><b>Project Management Team</b></p>"
                    + "</div></body></html>";

            try {
                emailService.sendHtmlEmail(clientEmail, "Project Request Rejected", content);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            return savedRequest;
        }
        return null;
    }
    @Autowired
    private OpenAiService openAiService;

    public Request createRequest(Request request) {
        // Appeler l’API OpenAI pour analyser la description
        String analysis = openAiService.analyzeProjectDescription(request.getDescription());

        // Ajouter le résultat de l’analyse dans l’entité
        request.setAnalysisResult(analysis);

        // Sauvegarder dans la base de données
        return projectRequestRepository.save(request);
    }
    public String analyzeProjectDescription1(String description) {
        return openAiService.analyzeProjectDescription(description);
    }
    public String analyzeProjectDescription(String description) {
        try {
            System.out.println("Début de l'analyse pour la description: " + description);

            // Détection de la langue de la description (simplifiée ici pour l'exemple)
            String language = detectLanguage(description);

            // Mots-clés pour chaque langue
            Map<String, String[]> keywords = new HashMap<>();
            keywords.put("fr", new String[]{"immeuble", "étages", "résidentiel", "parking souterrain", "fondations", "maison", "bâtiment", "construction", "infrastructure"});
            keywords.put("en", new String[]{"building", "floors", "residential", "underground parking", "foundations", "house", "building", "construction", "infrastructure"});
            keywords.put("es", new String[]{"edificio", "pisos", "residencial", "estacionamiento subterráneo", "cimientos", "casa", "edificio", "construcción", "infraestructura"});

            // Appeler la méthode d'analyse en fonction de la langue détectée
            if (language.equals("fr")) {
                return analyzeInFrench(description, keywords.get("fr"));
            } else if (language.equals("en")) {
                return analyzeInEnglish(description, keywords.get("en"));
            } else if (language.equals("es")) {
                return analyzeInSpanish(description, keywords.get("es"));
            } else {
                return "La langue du projet n'est pas supportée.";
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de l'analyse de la description : " + e.getMessage());
            e.printStackTrace();
            return "L'analyse a échoué.";
        }
    }

    // Méthode de détection simplifiée de la langue
    private String detectLanguage(String description) {
        if (description.matches(".*[à-ÿ].*")) {
            return "fr";  // Supposons que les caractères accentués appartiennent au français
        } else if (description.matches(".*[a-zA-Z].*")) {
            return "en";  // Supposons que l'anglais contient des caractères latins
        } else if (description.matches(".*[a-zA-Zñ].*")) {
            return "es";  // Supposons que l'espagnol utilise le "ñ"
        }
        return "unknown";
    }

    // Analyser en français
    private String analyzeInFrench(String description, String[] keywords) {
        if (containsKeywords(description, keywords)) {
            return "Le projet semble pertinent pour une construction immobilière.";
        } else {
            return "Le projet ne semble pas correspondre aux critères de construction. Veuillez préciser davantage.";
        }
    }

    // Analyser en anglais
    private String analyzeInEnglish(String description, String[] keywords) {
        if (containsKeywords(description, keywords)) {
            return "The project seems relevant for a building construction.";
        } else {
            return "The project does not seem to match the construction criteria. Please provide more details.";
        }
    }

    // Analyser en espagnol
    private String analyzeInSpanish(String description, String[] keywords) {
        if (containsKeywords(description, keywords)) {
            return "El proyecto parece relevante para una construcción de edificio.";
        } else {
            return "El proyecto no parece coincidir con los criterios de construcción. Por favor, proporcione más detalles.";
        }
    }

    // Vérification des mots-clés dans la description
    private boolean containsKeywords(String description, String[] keywords) {
        for (String keyword : keywords) {
            if (description.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public List<Request> getRequestsByIds(List<Long> ids) {
        return projectRequestRepository.findAllById(ids);
    }





}
