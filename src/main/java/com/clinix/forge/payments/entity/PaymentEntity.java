package com.clinix.forge.payments.entity;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.treatment.TreatmentEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_treatment", columnList = "treatment_id")
        }
)
@Getter
@Setter
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
    private String reference = "";

    @Column(columnDefinition = "TEXT", name = "treatment_details")
    private String treatmentDetails;

    @Column(nullable = false, name = "received_date")
    private LocalDate receivedDate;
}
