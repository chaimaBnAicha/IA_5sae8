package com.example.backend.services;

import com.example.backend.entities.Bill;
import java.util.List;

public interface IBillService {
    Bill addBill(Bill bill);
    List<Bill> getAllBills();
    Bill getBillById(Integer id);
    Bill updateBill(Integer id, Bill bill);
    boolean deleteBill(Integer id);
    List<Bill> getBillsByPaymentMode(Bill.PaymentMode paymentMode);
}
