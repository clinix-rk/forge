package com.clinix.forge.storage.entity;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity mapping representing files (e.g. documents, images) uploaded for patients.
 */
@Entity
@Table(name = "files")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", unique = true, nullable = false)
    private PatientEntity patient;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 512)
    private String location;
}
