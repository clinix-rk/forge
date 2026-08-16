package com.clinix.forge.prescription.entity;

import com.clinix.forge.catalog.medicines.MedicineEntity;
import com.clinix.forge.catalog.prescription.dosages.DosageEntity;
import com.clinix.forge.catalog.prescription.instructions.InstructionEntity;
import com.clinix.forge.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity mapping that links medicines to prescriptions with a specific dosage and quantity.
 */
@Entity
@Table(
        name = "prescription_medicines",
        indexes = {
                @Index(name = "idx_presc_med", columnList = "prescription_id, medicine_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_prescription_medicine",
                        columnNames = {"prescription_id", "medicine_id"}
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionMedicineEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private PrescriptionEntity prescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private MedicineEntity medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dosage_id")
    private DosageEntity dosage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instruction_id")
    private InstructionEntity instruction;

    @Column(nullable = false)
    private Integer quantity;
}
