package com.clinix.forge.patient.entity;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.doctors.DoctorEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "patients",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_doctor_serial", columnNames = {"doctor_id", "serial"})
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientEntity extends BaseEntity {

    @Column(name = "case_no", nullable = false, unique = true, updatable = false)
    private String caseNo;

    @Column(nullable = false)
    private Integer serial;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(length = 100)
    private String email;

    @Column
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 10)
    private String pincode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorEntity doctor;

    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhoneNumberEntity> phoneNumbers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "patient_medical_conditions",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    @Builder.Default
    private Set<MedicalConditionEntity> medicalConditions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "patient_drug_allergies",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "allergy_id")
    )
    @Builder.Default
    private Set<DrugAllergyEntity> drugAllergies = new HashSet<>();

    @Column(name = "referred_by", length = 50)
    private String referredBy;
}
