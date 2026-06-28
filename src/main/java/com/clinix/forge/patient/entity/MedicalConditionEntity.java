package com.clinix.forge.patient.entity;
import com.clinix.forge.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medical_conditions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalConditionEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "medicalConditions", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PatientEntity> patients = new HashSet<>();
}
