package com.example.backend.repositories;

import com.example.backend.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StockRepository extends JpaRepository<Stock,Integer> {
    // Nombre total de produits par catégorie
    @Query("SELECT s.category, COUNT(s) FROM Stock s GROUP BY s.category")
    List<Object[]> countByCategory();
    
    // Valeur totale du stock par catégorie
    @Query("SELECT s.category, SUM(s.quantity * s.unitPrice) FROM Stock s GROUP BY s.category")
    List<Object[]> totalValueByCategory();
    
    // Produits dont le stock est faible (moins de 10 unités)
    @Query("SELECT s FROM Stock s WHERE s.quantity < 10")
    List<Stock> findLowStockProducts();
    
    // Valeur totale de tout le stock
    @Query("SELECT SUM(s.quantity * s.unitPrice) FROM Stock s")
    Double getTotalStockValue();
    
    // Moyenne des prix par catégorie
    @Query("SELECT s.category, AVG(s.unitPrice) FROM Stock s GROUP BY s.category")
    List<Object[]> averagePriceByCategory();
}
