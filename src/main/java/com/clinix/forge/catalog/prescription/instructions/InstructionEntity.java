package com.clinix.forge.catalog.prescription.instructions;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.prescription.entity.PrescriptionMedicineEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity mapping representing predefined drug dosages.
 */
@Entity
@Table(name = "medicine_instruction")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstructionEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String instruction;

    @Builder.Default
    @OneToMany(mappedBy = "instruction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PrescriptionMedicineEntity> prescriptionMedicines = new HashSet<>();
}
