package com.clinix.forge.treatment;

import com.clinix.forge.catalog.treatments.TreatmentCategoryEntity;
import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.finance.entity.PaymentEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Entity mapping representing a patient treatment.
 */
@Entity
@Table(name = "treatments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_category_id", nullable = false)
    private TreatmentCategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @OneToOne(mappedBy = "treatment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PaymentEntity payment;
}
