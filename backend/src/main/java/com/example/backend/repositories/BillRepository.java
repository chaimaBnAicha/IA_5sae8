package com.example.backend.repositories;

import com.example.backend.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository    extends JpaRepository<Bill, Integer> {
    List<Bill> findByPaymentMode(Bill.PaymentMode paymentMode);

}
