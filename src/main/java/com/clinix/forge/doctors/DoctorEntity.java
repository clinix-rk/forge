package com.clinix.forge.doctors;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Doctor entity stored in DB
 */
@Entity
@Table(name = "doctors")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorEntity extends BaseEntity {

    /**
     * Case number prefix to be prepended to the Case number and the Receipt number
     */
    @Column(name = "case_no_prefix", nullable = false, length = 1, unique = true)
    private String caseNoPrefix;

    /**
     * Name of the doctor
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Patients related to the doctor
     */
    @Builder.Default
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatientEntity> patients = new HashSet<>();
}
