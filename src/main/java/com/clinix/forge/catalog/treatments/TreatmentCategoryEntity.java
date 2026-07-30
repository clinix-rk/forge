package com.clinix.forge.catalog.treatments;

import com.clinix.forge.core.entity.BaseEntity;
import com.clinix.forge.treatment.TreatmentEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity mapping representing categories of treatment with a tree hierarchy.
 */
@Entity
@Table(
        name = "treatment_categories",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_treatment_category_name_parent",
                columnNames = {"name", "parent_id"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentCategoryEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private TreatmentCategoryEntity parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TreatmentCategoryEntity> children = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TreatmentEntity> treatments = new HashSet<>();
}
