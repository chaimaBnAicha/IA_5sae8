package com.example.backend.controllers;

import com.example.backend.DTOs.RequestDTO;
import com.example.backend.entities.Request;
import com.example.backend.entities.Statut;
import com.example.backend.entities.User;
import com.example.backend.repositories.ProjectRequestRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.ProjectRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class ProjectRequestController {

    @Autowired
    private ProjectRequestService projectRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRequestRepository projectRequestRepository;

    // 🔁 Convert Request → DTO
    private RequestDTO convertToDTO(Request request) {
        RequestDTO dto = new RequestDTO();
        dto.setId_projet(request.getId_projet());
        dto.setProjectName(request.getProjectName());
        dto.setDescription(request.getDescription());
        dto.setEstimated_budget(request.getEstimated_budget());
        dto.setEstimated_duration(request.getEstimated_duration());
        dto.setGeographic_location(request.getGeographic_location());
        dto.setRecommendationScore(request.getRecommendationScore());
        dto.setAnalysisResult(request.getAnalysisResult());
        dto.setStatus(request.getStatus() != null ? request.getStatus().name() : null);
        dto.setPriority(String.valueOf(request.getPriority()));
        if (request.getUser() != null) {
            dto.setUser_id(request.getUser().getId());
        }
        return dto;
    }

    @PostMapping("/analyze-description")
    public ResponseEntity<Map<String, String>> analyzeDescription(@RequestBody Map<String, String> requestBody) {
        String description = requestBody.get("description");

        if (description == null || description.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Description du projet manquante"));
        }

        String analysisResult = projectRequestService.analyzeProjectDescription(description);
        System.out.println("Analysis Result: " + analysisResult);

        if ("L'analyse a échoué.".equals(analysisResult)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur d'analyse. Détails : " + analysisResult));
        }

        Map<String, String> response = new HashMap<>();
        response.put("analysis", analysisResult);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/RequestsPost")
    public ResponseEntity<Request> createProjectRequest(@Valid @RequestBody Request projectRequest) {
        System.out.println("test work");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Authenticated username: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + username));

        System.out.println("User found: " + user.getUsername());

        projectRequest.setUser(user);
        String analysis = projectRequestService.analyzeProjectDescription(projectRequest.getDescription());
        projectRequest.setAnalysisResult(analysis);

        Request savedRequest = projectRequestService.createProjectRequest(projectRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }

    // ✅ Modified: Return DTO instead of full Entity
    @GetMapping("/AllRequest")
    public List<RequestDTO> getAllRequest() {
        return projectRequestService.getAllRequests().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/request/{id_projet}")
    public ResponseEntity<RequestDTO> getRequestById(@PathVariable Long id_projet) {
        Request request = projectRequestService.getRequestById(id_projet);
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(request));
    }

    // ✅ Modified: Return DTO list
    @GetMapping("/requests/status/{status}")
    public List<RequestDTO> getRequestsByStatus(@PathVariable Statut status) {
        return projectRequestService.getRequestsByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/requests/search")
    public List<RequestDTO> searchRequests(@RequestParam String query) {
        return projectRequestService.searchRequests(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/request/{id_project}")
    public ResponseEntity<Request> updateRequest(@PathVariable Long id_project, @Valid @RequestBody Request request) {
        Request existingRequest = projectRequestService.getRequestById(id_project);
        if (existingRequest == null) {
            return ResponseEntity.notFound().build();
        }

        existingRequest.setProjectName(request.getProjectName());
        existingRequest.setDescription(request.getDescription());
        existingRequest.setEstimated_budget(request.getEstimated_budget());
        existingRequest.setEstimated_duration(request.getEstimated_duration());
        existingRequest.setGeographic_location(request.getGeographic_location());

        Request updatedRequest = projectRequestService.updateRequest(existingRequest);
        return ResponseEntity.ok(updatedRequest);
    }

    @PutMapping("/request/{id}/approve")
    public ResponseEntity<Request> approveRequest(@PathVariable Long id) {
        Request updatedRequest = projectRequestService.approveRequest(id);
        return (updatedRequest != null) ? ResponseEntity.ok(updatedRequest) : ResponseEntity.notFound().build();
    }

    @PutMapping("/request/{id}/reject")
    public ResponseEntity<Request> rejectRequest(@PathVariable Long id) {
        Request updatedRequest = projectRequestService.rejectRequest(id);
        return (updatedRequest != null) ? ResponseEntity.ok(updatedRequest) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/request/{id_project}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id_project) {
        Request existingRequest = projectRequestService.getRequestById(id_project);
        if (existingRequest == null) {
            return ResponseEntity.notFound().build();
        }
        projectRequestService.deleteRequest(id_project);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getProjectRequestStatistics() {
        long pending = projectRequestRepository.countByStatus(Statut.Pending);
        long approved = projectRequestRepository.countByStatus(Statut.Approved);
        long rejected = projectRequestRepository.countByStatus(Statut.Rejected);

        double totalPendingBudget = projectRequestRepository.sumBudgetByStatus(Statut.Pending);
        double totalApprovedBudget = projectRequestRepository.sumBudgetByStatus(Statut.Approved);
        double totalRejectedBudget = projectRequestRepository.sumBudgetByStatus(Statut.Rejected);

        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", pending);
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        stats.put("totalPendingBudget", totalPendingBudget);
        stats.put("totalApprovedBudget", totalApprovedBudget);
        stats.put("totalRejectedBudget", totalRejectedBudget);

        return ResponseEntity.ok(stats);
    }
}
