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
public class Advance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    double amount_request;
    Date requestDate;
    String reason;
    @Enumerated(EnumType.STRING)
    AdvanceStatus status;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    User user;
}
