package com.clinix.forge.user.dto;

import com.clinix.forge.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a new user account")
public record CreateUserRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "Unique username for the account", example = "admin_doctor")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        @Schema(description = "Security password for the account", example = "SecurePass123!")
        String password,

        @NotNull(message = "Role is required")
        @Schema(description = "Role assigned to the user", example = "ADMIN")
        Role role
) {
}
