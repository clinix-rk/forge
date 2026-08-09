package com.clinix.forge.catalog.complains;

import com.clinix.forge.complain.ComplainEntity;
import com.clinix.forge.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for complain categories supporting hierarchy (parent-children relations).
 */
@Entity
@Table(
        name = "complain_categories",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_complain_category_name_parent",
                columnNames = {"name", "parent_id"}
        )
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplainCategoryEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ComplainCategoryEntity parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ComplainCategoryEntity> children = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ComplainEntity> complains = new HashSet<>();
}
