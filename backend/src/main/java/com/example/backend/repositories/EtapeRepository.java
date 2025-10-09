package com.example.backend.repositories;

import com.example.backend.entities.Etape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtapeRepository extends JpaRepository<Etape, Long> {
    List<Etape> findByTacheId(Long tacheId);
}
