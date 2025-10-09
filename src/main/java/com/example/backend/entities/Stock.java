package com.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
//@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
@Getter
@Setter
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_stock;

    @NotBlank(message = "Le nom du stock est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String name;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    @Min(value = 0, message = "La quantité ne peut pas être négative")
    @NotNull(message = "La quantité est obligatoire")
    private int quantity;

    @DecimalMin(value = "0.0", message = "Le prix unitaire ne peut pas être négatif")
    @NotNull(message = "Le prix unitaire est obligatoire")
    private double unitPrice;

    @NotNull(message = "La catégorie est obligatoire")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Version
    private Long version;

    public enum Category{MATERIALS,TOOLS,ELECTRICAL_PLUMBING}
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy="stock")
    @JsonManagedReference
    private Set<Bill> Bills = new HashSet<>();
}
