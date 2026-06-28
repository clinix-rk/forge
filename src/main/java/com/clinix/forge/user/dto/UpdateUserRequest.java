package com.clinix.forge.user.dto;

import com.clinix.forge.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating an existing user account")
public record UpdateUserRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "Updated username for the account", example = "admin_doctor_updated")
        String username,

        @Size(max = 100, message = "Password must not exceed 100 characters")
        @Schema(description = "Optional updated security password for the account (leave blank if password is not changing)", example = "NewSecurePass123!")
        String password,

        @NotNull(message = "Role is required")
        @Schema(description = "Updated role assigned to the user", example = "ADMIN")
        Role role
) {}
