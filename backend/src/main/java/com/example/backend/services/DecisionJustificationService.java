package com.example.backend.services;

import com.example.backend.entities.DecisionJustification;
import com.example.backend.entities.Request;
import com.example.backend.repositories.DecisionJustificationRepository;
import com.example.backend.repositories.ProjectRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DecisionJustificationService {

    @Autowired
    private DecisionJustificationRepository justificationRepository;

    @Autowired
    private ProjectRequestRepository requestRepository;

    public DecisionJustification saveJustification(Long requestId, String justificationText) {
        Optional<Request> requestOpt = requestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            DecisionJustification justification = new DecisionJustification();
            justification.setJustificationText(justificationText);
            justification.setRequest(requestOpt.get());
            return justificationRepository.save(justification);
        } else {
            throw new RuntimeException("Request not found");
        }
    }

    public DecisionJustification getJustificationByRequestId(Long requestId) {
        return justificationRepository.findByRequestId(requestId);
    }
}