package com.clinix.forge.appointment;

import com.clinix.forge.appointment.dto.AppointmentResponse;
import com.clinix.forge.appointment.dto.CreateAppointmentRequest;
import com.clinix.forge.appointment.dto.UpdateAppointmentRequest;
import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "Endpoints for managing patient appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Schedule an appointment", description = "Creates a new patient appointment.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Appointment time conflict")
    })
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @RequestBody @Valid CreateAppointmentRequest request
    ) {
        log.debug("API call: Create a new appointment record");
        AppointmentResponse response = appointmentService.createAppointment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment scheduled successfully.", response));
    }

    @GetMapping
    @Operation(summary = "Get appointments (Paginated)", description = "Retrieves a paginated list of all appointments.")
    public ResponseEntity<ApiResponse<PaginatedPayload<AppointmentResponse>>> getAllAppointments(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize
    ) {
        log.debug("API call: Fetching appointments paginated - Page: {}, Size: {}", pageNo, pageSize);
        PaginatedPayload<AppointmentResponse> appointments = appointmentService.getAllAppointments(pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Appointments retrieved successfully.", appointments));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieves an appointment's details by database ID.")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(@PathVariable Long id) {
        log.debug("API call: Fetching appointment with ID: {}", id);
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Appointment retrieved successfully.", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment by ID", description = "Updates an existing appointment's details.")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointmentById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAppointmentRequest request
    ) {
        log.debug("API call: Updating appointment with ID: {}", id);
        AppointmentResponse updatedAppointment = appointmentService.updateAppointmentById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Appointment updated successfully.", updatedAppointment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete appointment by ID", description = "Deletes an appointment record based on ID.")
    public ResponseEntity<Void> deleteAppointmentById(@PathVariable Long id) {
        log.debug("API call: Deleting appointment with ID: {}", id);
        appointmentService.deleteAppointmentById(id);
        return ResponseEntity.noContent().build();
    }
}
