package com.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
//@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
@Getter
@Setter
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_Bill;

    private String num_Bill;
    private LocalDate date;
    private double total_Amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;
    
    // Il y a une confusion dans la définition des énumérations
    // Status concerne le mode de paiement (CASH, TRANSFER, etc.)
    // PaymentMode concerne l'état du paiement (PAID, PENDING, etc.)
    public enum Status{CASH, TRANSFER, CHECK, BANK_CARD}
    public enum PaymentMode{PAID, PENDING, CANCELLED}
    
    @ManyToOne
    @JsonBackReference
    Stock stock;

}
