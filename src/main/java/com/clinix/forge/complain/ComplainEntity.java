package com.clinix.forge.complain.entity;

import com.clinix.forge.catalog.complains.ComplainCategoryEntity;
import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Entity mapping representing a patient complain.
 */
@Entity
@Table(name = "complains")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplainEntity extends BaseEntity {

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String details;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complain_category_id", nullable = false)
    private ComplainCategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;
}
