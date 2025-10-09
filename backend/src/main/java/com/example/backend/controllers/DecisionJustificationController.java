package com.example.backend.controllers;

import com.example.backend.entities.DecisionJustification;
import com.example.backend.services.DecisionJustificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/justifications")
@CrossOrigin(origins = "http://localhost:4200")
public class DecisionJustificationController {

    @Autowired
    private DecisionJustificationService justificationService;

    @PostMapping("/add/{requestId}")
    public ResponseEntity<DecisionJustification> addJustification(
            @PathVariable Long requestId,
            @RequestBody String justificationText) {

        try {
            DecisionJustification justification = justificationService.saveJustification(requestId, justificationText);
            return ResponseEntity.status(justification.getId() == null ? HttpStatus.CREATED : HttpStatus.OK).body(justification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @GetMapping("/{requestId}")
    public DecisionJustification getJustification(@PathVariable Long requestId) {
        return justificationService.getJustificationByRequestId(requestId);
    }
}