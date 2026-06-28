package com.clinix.forge.appointment.entity;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity mapping representing scheduled appointments for patients.
 */
@Entity
@Table(
        name = "appointments",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_patient_datetime",
                columnNames = {"patient_id", "datetime"}
        ),
        indexes = {
                @Index(name = "idx_patient_datetime", columnList = "patient_id, datetime")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private LocalDateTime datetime;
}
