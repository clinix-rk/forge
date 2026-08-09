package com.clinix.forge.doctors;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorEntity extends BaseEntity {

    @Column(name = "case_no_prefix", nullable = false, length = 1, unique = true)
    private String caseNoPrefix;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "total_patients", nullable = false)
    @Builder.Default
    private Integer totalPatients = 0;

    @Builder.Default
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatientEntity> patients = new HashSet<>();
}