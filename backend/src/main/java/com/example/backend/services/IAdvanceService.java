package com.example.backend.services;

import com.example.backend.entities.Advance;

import java.util.List;
import java.util.Map;

public interface IAdvanceService {
    Advance addAdvance(Advance advance);
    void deleteAdvance(int idAdvance);
    Advance updateAdvance(Advance advance);
    List<Advance> allAdvances();
    Advance findAdvanceById(int idAdvance);
    List<Advance> getAllAdvancesByUser(int userId);
    boolean canApproveAdvance(int userId, int advanceId);
    //Advance updateAdvanceStatus(int id, String status);
    //List<Double> getSinusoidalData();
    Map<String, Long> getAdvancesByMonth();
    Map<String, Long> getAdvancesByStatus();
    Map<String, Double> getSinusoidalData();
}
