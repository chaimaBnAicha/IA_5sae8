package com.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id_offer;
    String Title;
    String Description;
    Date Start_Date;
    Date End_Date;
    @Enumerated(EnumType.STRING)
    TypeOffer Typeoffer;
    @Enumerated(EnumType.STRING)
    OfferStatus Status;

   /* @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    User user;*/


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
