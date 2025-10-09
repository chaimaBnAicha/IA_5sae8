package com.example.backend.repositories;

import com.example.backend.entities.Request;
import com.example.backend.entities.Statut;
import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByUserId(Long userId);
    List<Request> findByStatus(Statut status);
    List<Request> findByProjectNameContainingIgnoreCase(String project_name);
    long countByStatus(Statut status);
    @Query("SELECT SUM(r.estimated_budget) FROM Request r WHERE r.status = :status")
    double sumBudgetByStatus(@Param("status") Statut status);




}