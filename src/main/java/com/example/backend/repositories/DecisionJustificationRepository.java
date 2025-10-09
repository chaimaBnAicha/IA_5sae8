package com.example.backend.repositories;



import com.example.backend.entities.DecisionJustification;
import com.example.backend.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DecisionJustificationRepository extends JpaRepository<DecisionJustification, Long> {
    @Query("SELECT dj FROM DecisionJustification dj WHERE dj.request.id_projet = :requestId")
    DecisionJustification findByRequestId(@Param("requestId") Long requestId);

}