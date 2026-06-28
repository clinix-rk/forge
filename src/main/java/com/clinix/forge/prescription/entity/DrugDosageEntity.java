package com.clinix.forge.prescription.entity;

import com.clinix.forge.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity mapping representing predefined drug dosages.
 */
@Entity
@Table(name = "drug_dosages")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrugDosageEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String dosage;

    @Builder.Default
    @OneToMany(mappedBy = "dosage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PrescriptionMedicineEntity> prescriptionMedicines = new HashSet<>();
}
