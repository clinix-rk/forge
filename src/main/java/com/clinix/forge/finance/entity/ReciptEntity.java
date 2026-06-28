package com.clinix.forge.finance.entity;

import com.clinix.forge.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity mapping representing recipts issued to patients.
 * Mapped using spelling matching the database schema ("Recipt" instead of "Receipt").
 */
@Entity
@Table(
        name = "recipts",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_recipt_financial_doctor_serial",
                columnNames = {"financial_year", "doctor_identity_character", "serial"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReciptEntity extends BaseEntity {

    @Column(name = "doctor_identity_character", nullable = false, length = 1)
    private String doctorIdentityCharacter;

    @Column(name = "financial_year", nullable = false, length = 50)
    private String financialYear;

    @Column(nullable = false)
    private Integer serial;

    @OneToMany(mappedBy = "recipt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PaymentEntity> payments = new ArrayList<>();
}
