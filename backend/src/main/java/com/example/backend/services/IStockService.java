package com.example.backend.services;

import com.example.backend.entities.Stock;

import java.util.List;
import java.util.Map;

public interface IStockService {
    Stock addStock(Stock stock);
    List<Stock> getAllStocks();
    Stock getStockById(Integer id);
    Stock updateStock(Integer id, Stock stock);
    boolean deleteStock(Integer id);
    Map<String, Long> getStockCountByCategory();
    Map<String, Double> getTotalValueByCategory();
    List<Stock> getLowStockProducts();
    Double getTotalStockValue();
    Map<String, Double> getAveragePriceByCategory();
}
