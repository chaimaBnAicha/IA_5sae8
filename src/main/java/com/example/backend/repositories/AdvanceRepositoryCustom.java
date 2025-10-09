package com.example.backend.repositories;

public interface AdvanceRepositoryCustom {
    boolean canApproveAdvance(int userId, int advanceId, double maxAdvanceLimit);
}
