package com.clinix.forge.patient.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drug_allergies")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrugAllergyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
