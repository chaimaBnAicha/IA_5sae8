package com.example.backend.services;

import com.example.backend.entities.Bill;
import com.example.backend.entities.Stock;
import com.example.backend.repositories.BillRepository;
import com.example.backend.repositories.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BillService implements IBillService {

    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private StockRepository stockRepository;

    @Override
    @Transactional
    public Bill addBill(Bill bill) {
        validateBill(bill);
        return billRepository.save(bill);
    }

    @Override
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    @Override
    public Bill getBillById(Integer id) {
        return billRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Facture avec ID " + id + " non trouvée"));
    }
    @Override
    public List<Bill> getBillsByPaymentMode(Bill.PaymentMode paymentMode) {
        return billRepository.findByPaymentMode(paymentMode);
    }
    @Override
    @Transactional
    public Bill updateBill(Integer id, Bill bill) {
        Bill existingBill = billRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Facture avec ID " + id + " non trouvée"));
        
        validateBill(bill);
        
        existingBill.setNum_Bill(bill.getNum_Bill());
        existingBill.setDate(bill.getDate());
        existingBill.setTotal_Amount(bill.getTotal_Amount());
        existingBill.setStatus(bill.getStatus());
        existingBill.setPaymentMode(bill.getPaymentMode());
        existingBill.setStock(bill.getStock());
        
        return billRepository.save(existingBill);
    }

    @Override
    @Transactional
    public boolean deleteBill(Integer id) {
        if (billRepository.existsById(id)) {
            billRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private void validateBill(Bill bill) {
        if (bill.getStock() != null && bill.getStock().getId_stock() != null) {
            Stock stock = stockRepository.findById(bill.getStock().getId_stock())
                .orElseThrow(() -> new EntityNotFoundException("Stock avec ID " + bill.getStock().getId_stock() + " non trouvé"));
            
            // Pour éviter des problèmes d'entités détachées, on utilise l'entité récupérée
            bill.setStock(stock);
        }
        
        if (bill.getTotal_Amount() < 0) {
            throw new IllegalArgumentException("Le montant total ne peut pas être négatif");
        }
        
        // Vérifier que les valeurs d'énumération sont valides
        if (bill.getStatus() == null) {
            throw new IllegalArgumentException("Le statut de la facture est obligatoire");
        }
        
        if (bill.getPaymentMode() == null) {
            throw new IllegalArgumentException("Le mode de paiement est obligatoire");
        }
        
        // Vérifier que les valeurs sont dans les limites des énumérations
        try {
            Bill.Status.valueOf(bill.getStatus().name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Statut de facture invalide. Les valeurs valides sont: " 
                + String.join(", ", getAllEnumValues(Bill.Status.class)));
        }
        
        try {
            Bill.PaymentMode.valueOf(bill.getPaymentMode().name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Mode de paiement invalide. Les valeurs valides sont: " 
                + String.join(", ", getAllEnumValues(Bill.PaymentMode.class)));
        }
    }
    
    // Méthode utilitaire pour obtenir toutes les valeurs d'une énumération
    private <E extends Enum<E>> String[] getAllEnumValues(Class<E> enumClass) {
        return java.util.Arrays.stream(enumClass.getEnumConstants())
                              .map(Enum::name)
                              .toArray(String[]::new);
    }
}
