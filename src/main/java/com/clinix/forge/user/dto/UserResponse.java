package com.clinix.forge.user.dto;

import com.clinix.forge.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Represents user account details in the system")
public record UserResponse(
        @Schema(description = "Unique ID of the user record", example = "1")
        Long id,

        @Schema(description = "Username of the account", example = "admin_doctor")
        String username,

        @Schema(description = "Role assigned to the user", example = "ADMIN")
        Role role,

        @Schema(description = "Timestamp when the user account was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the user account was last updated")
        Instant updatedAt
) {}
