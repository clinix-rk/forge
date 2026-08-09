package com.clinix.forge.user;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginationMetadata;
import com.clinix.forge.user.dto.CreateUserRequest;
import com.clinix.forge.user.dto.UpdateUserRequest;
import com.clinix.forge.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing system users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new system user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody @Valid CreateUserRequest request
    ) {
        log.debug("API call: Create a new user record");
        UserResponse response = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get users (Paginated)", description = "Retrieves a paginated list of all users.")
    public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize
    ) {
        log.debug("API call: Fetching users paginated - Page: {}, Size: {}", pageNo, pageSize);
        Page<UserResponse> users = userService.getAllUsers(pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(users.getContent(), new PaginationMetadata(users.getNumber(), users.getSize(), users.getTotalElements(), users.getTotalPages(), users.hasNext(), users.hasPrevious())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a single user's details by database ID.")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.debug("API call: Fetching user with ID: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID", description = "Updates an existing user's details.")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest request
    ) {
        log.debug("API call: Updating user with ID: {}", id);
        UserResponse updatedUser = userService.updateUserById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID", description = "Deletes a user record based on ID.")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        log.debug("API call: Deleting user with ID: {}", id);
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
