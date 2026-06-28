package com.clinix.forge.patient.entity;

import com.clinix.forge.core.entity.BaseEntity;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "phone_numbers",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_patient_phone_type",
                columnNames = {
                        "patient_id",
                        "type"
                }
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumberEntity extends BaseEntity {

        @Column(name = "phone_number", length = 20, nullable = false)
        private String phoneNumber;

        @Enumerated(value = EnumType.STRING)
        @Column(nullable = false)
        private PhoneType type;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "patient_id", nullable = false)
        private PatientEntity patient;
}
