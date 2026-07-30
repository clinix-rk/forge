package com.clinix.forge.finance.entity;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.treatment.TreatmentEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_treatment", columnList = "treatment_id")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @Column(name = "doctor_identity_character", nullable = false, length = 1)
    private String doctorIdentityCharacter;

    @Column(name = "financial_year", nullable = false, length = 50)
    private String financialYear;

    @Column(nullable = false)
    private Integer serial;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_id", unique = true, nullable = false)
    private TreatmentEntity treatment;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentMethod method;

    @Column(nullable = false, length = 255)
    @Builder.Default
    private String reference = "";
}
