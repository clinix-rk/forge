package com.clinix.forge.suggestion.entity;

import com.clinix.forge.catalog.treatments.TreatmentCategoryEntity;
import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * JPA entity representing suggestions proposed to patients.
 */
@Entity
@Table(name = "suggestions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionEntity extends BaseEntity {

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "suggestion_category_id", nullable = false)
    private TreatmentCategoryEntity category;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private Integer cost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private SuggestionStatus status = SuggestionStatus.SUGGESTED;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;
}
