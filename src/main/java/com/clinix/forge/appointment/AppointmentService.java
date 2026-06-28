package com.clinix.forge.appointment;

import com.clinix.forge.appointment.dto.CreateAppointmentRequest;
import com.clinix.forge.appointment.dto.AppointmentResponse;
import com.clinix.forge.appointment.dto.UpdateAppointmentRequest;
import com.clinix.forge.appointment.entity.AppointmentEntity;
import com.clinix.forge.appointment.exception.AppointmentConflictException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional(rollbackFor = Exception.class)
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        log.info("Creating appointment for patient ID: {} at {}", request.patientId(), request.datetime());

        PatientEntity patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.patientId()));

        if (appointmentRepository.findByPatientIdAndDatetime(request.patientId(), request.datetime()).isPresent()) {
            log.warn("Appointment conflict: patient ID {} already has an appointment at {}", request.patientId(), request.datetime());
            throw new AppointmentConflictException("Patient already has an appointment at " + request.datetime());
        }

        AppointmentEntity entity = appointmentMapper.toEntity(request);
        entity.setPatient(patient);

        AppointmentEntity savedAppointment = appointmentRepository.save(entity);
        log.info("Appointment created successfully with ID: {}", savedAppointment.getId());
        return appointmentMapper.toResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<AppointmentResponse> getAllAppointments(int pageNo, int pageSize) {
        log.debug("Fetching appointments - PageNo: {}, PageSize: {}", pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<AppointmentEntity> appointmentPage = appointmentRepository.findAll(pageRequest);

        List<AppointmentResponse> responses = appointmentPage.getContent().stream()
                .map(appointmentMapper::toResponse)
                .toList();

        return PaginatedPayload.of(responses, appointmentPage);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        log.debug("Fetching appointment with ID: {}", id);
        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        return appointmentMapper.toResponse(appointment);
    }

    @Transactional(rollbackFor = Exception.class)
    public AppointmentResponse updateAppointmentById(Long id, UpdateAppointmentRequest request) {
        log.info("Updating appointment with ID: {}", id);
        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        // Check for conflict if datetime changes
        if (!appointment.getDatetime().equals(request.datetime()) &&
                appointmentRepository.findByPatientIdAndDatetime(appointment.getPatient().getId(), request.datetime()).isPresent()) {
            throw new AppointmentConflictException("Patient already has an appointment at " + request.datetime());
        }

        appointmentMapper.updateEntityFromRequest(request, appointment);

        AppointmentEntity updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment updated successfully with ID: {}", updatedAppointment.getId());
        return appointmentMapper.toResponse(updatedAppointment);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAppointmentById(Long id) {
        log.info("Deleting appointment with ID: {}", id);
        if (!appointmentRepository.existsById(id)) {
            log.warn("Appointment not found for deletion with ID: {}", id);
            throw new ResourceNotFoundException("Appointment not found with ID: " + id);
        }
        appointmentRepository.deleteById(id);
        log.info("Appointment deleted successfully: {}", id);
    }
}
