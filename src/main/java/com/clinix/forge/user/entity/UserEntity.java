package com.clinix.forge.user.entity;

import com.clinix.forge.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class mapping to the database "users" table.
 * Extends BaseEntity to inherit auto-incrementing ID and creation/update timestamps.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;
}
