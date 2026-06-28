package com.clinix.forge.prescription.entity;

import com.clinix.forge.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity mapping representing medicine items.
 */
@Entity
@Table(
        name = "medicines",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_medicine_name_type",
                columnNames = {"name", "type"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicineEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 255)
    private String instruction;

    @Builder.Default
    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PrescriptionMedicineEntity> prescriptionMedicines = new HashSet<>();
}
