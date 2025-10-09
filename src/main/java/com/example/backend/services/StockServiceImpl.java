package com.example.backend.services;

import com.example.backend.entities.Stock;
import com.example.backend.repositories.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class StockServiceImpl implements IStockService {

    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Stock addStock(Stock stock) {
        validateStock(stock);
        Stock savedStock = stockRepository.save(stock);
        
        // Envoyer l'email de notification
        try {
            emailService.sendStockNotification(
                savedStock.getName(),
                "destinataire@email.com" // Email du destinataire
            );
        } catch (Exception e) {
            // Log l'erreur mais ne pas bloquer la création du stock
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
        
        return savedStock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Stock getStockById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du stock ne peut pas être null");
        }
        return stockRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Stock avec ID " + id + " non trouvé"));
    }

    @Override
    @Transactional
    public Stock updateStock(Integer id, Stock stock) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du stock ne peut pas être null");
        }
        
        Stock existingStock = stockRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Stock avec ID " + id + " non trouvé"));
        
        validateStock(stock);
        
        existingStock.setName(stock.getName());
        existingStock.setDescription(stock.getDescription());
        existingStock.setQuantity(stock.getQuantity());
        existingStock.setUnitPrice(stock.getUnitPrice());
        existingStock.setCategory(stock.getCategory());
        
        return stockRepository.save(existingStock);
    }

    @Override
    @Transactional
    public boolean deleteStock(Integer id_stock) {
        if (!stockRepository.existsById(id_stock)) {
            return false;
        }
        stockRepository.deleteById(id_stock);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getStockCountByCategory() {
        List<Object[]> results = stockRepository.countByCategory();
        Map<String, Long> countByCategory = new HashMap<>();
        
        if (results != null) {
            for (Object[] result : results) {
                if (result[0] != null && result[1] != null) {
                    countByCategory.put(((Stock.Category) result[0]).name(), (Long) result[1]);
                }
            }
        }
        
        return countByCategory;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getTotalValueByCategory() {
        List<Object[]> results = stockRepository.totalValueByCategory();
        Map<String, Double> valueByCategory = new HashMap<>();
        
        if (results != null) {
            for (Object[] result : results) {
                if (result[0] != null && result[1] != null) {
                    valueByCategory.put(((Stock.Category) result[0]).name(), (Double) result[1]);
                }
            }
        }
        
        return valueByCategory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> getLowStockProducts() {
        return stockRepository.findLowStockProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalStockValue() {
        Double value = stockRepository.getTotalStockValue();
        return value != null ? value : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getAveragePriceByCategory() {
        List<Object[]> results = stockRepository.averagePriceByCategory();
        Map<String, Double> avgByCategory = new HashMap<>();
        
        if (results != null) {
            for (Object[] result : results) {
                if (result[0] != null && result[1] != null) {
                    avgByCategory.put(((Stock.Category) result[0]).name(), (Double) result[1]);
                }
            }
        }
        
        return avgByCategory;
    }

    private void validateStock(Stock stock) {
        if (stock.getQuantity() > 10000) {
            throw new IllegalArgumentException("La quantité ne peut pas dépasser 10000 unités");
        }
        if (stock.getUnitPrice() > 100000) {
            throw new IllegalArgumentException("Le prix unitaire ne peut pas dépasser 100000");
        }
    }
}