package com.clinix.forge.appointment;

import com.clinix.forge.appointment.dto.CreateAppointmentRequest;
import com.clinix.forge.appointment.dto.UpdateAppointmentRequest;
import com.clinix.forge.appointment.dto.AppointmentResponse;
import com.clinix.forge.appointment.exception.AppointmentConflictException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.appointment.entity.AppointmentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTests {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private PatientEntity patientEntity;
    private AppointmentEntity appointmentEntity;
    private AppointmentResponse appointmentResponse;
    private CreateAppointmentRequest createRequest;
    private UpdateAppointmentRequest updateRequest;

    @BeforeEach
    public void setUp() {
        patientEntity = PatientEntity.builder().build();
        patientEntity.setId(1L);
        
        appointmentEntity = AppointmentEntity.builder()
                .patient(patientEntity)
                .datetime(LocalDateTime.of(2023, 1, 1, 10, 0))
                .notes("Checkup")
                .build();
        appointmentEntity.setId(10L);

        appointmentResponse = new AppointmentResponse(10L, 1L, "Checkup", LocalDateTime.of(2023, 1, 1, 10, 0), null, null);
        createRequest = new CreateAppointmentRequest(1L, "Checkup", LocalDateTime.of(2023, 1, 1, 10, 0));
        updateRequest = new UpdateAppointmentRequest("Follow-up", LocalDateTime.of(2023, 1, 2, 11, 0));
    }

    @Test
    public void createAppointment_Success() {
        when(patientRepository.findById(createRequest.patientId())).thenReturn(Optional.of(patientEntity));
        when(appointmentRepository.findByPatientIdAndDatetime(createRequest.patientId(), createRequest.datetime()))
                .thenReturn(Optional.empty());
        when(appointmentMapper.toEntity(createRequest)).thenReturn(appointmentEntity);
        when(appointmentRepository.save(any(AppointmentEntity.class))).thenReturn(appointmentEntity);
        when(appointmentMapper.toResponse(appointmentEntity)).thenReturn(appointmentResponse);

        AppointmentResponse result = appointmentService.createAppointment(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        verify(appointmentRepository).save(any(AppointmentEntity.class));
    }

    @Test
    public void createAppointment_PatientNotFound() {
        when(patientRepository.findById(createRequest.patientId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.createAppointment(createRequest));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    public void createAppointment_DuplicateDatetime() {
        when(patientRepository.findById(createRequest.patientId())).thenReturn(Optional.of(patientEntity));
        when(appointmentRepository.findByPatientIdAndDatetime(createRequest.patientId(), createRequest.datetime()))
                .thenReturn(Optional.of(appointmentEntity));
        assertThrows(AppointmentConflictException.class, () -> appointmentService.createAppointment(createRequest));
    }

    @Test
    public void getAppointmentById_Success() {
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointmentEntity));
        when(appointmentMapper.toResponse(appointmentEntity)).thenReturn(appointmentResponse);
        AppointmentResponse result = appointmentService.getAppointmentById(10L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    public void getAppointmentById_NotFound() {
        when(appointmentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.getAppointmentById(10L));
    }

    @Test
    public void getAllAppointments_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<AppointmentEntity> page = new PageImpl<>(List.of(appointmentEntity));
        when(appointmentRepository.findAll(pageRequest)).thenReturn(page);
        when(appointmentMapper.toResponse(appointmentEntity)).thenReturn(appointmentResponse);

        PaginatedPayload<AppointmentResponse> result = appointmentService.getAllAppointments(0, 10);
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().id()).isEqualTo(10L);
    }

    @Test
    public void updateAppointment_Success() {
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointmentEntity));
        // No conflict on new datetime
        when(appointmentRepository.findByPatientIdAndDatetime(patientEntity.getId(), updateRequest.datetime()))
                .thenReturn(Optional.empty());
        doNothing().when(appointmentMapper).updateEntityFromRequest(updateRequest, appointmentEntity);
        when(appointmentRepository.save(appointmentEntity)).thenReturn(appointmentEntity);
        when(appointmentMapper.toResponse(appointmentEntity)).thenReturn(appointmentResponse);

        AppointmentResponse result = appointmentService.updateAppointmentById(10L, updateRequest);
        assertThat(result).isNotNull();
        verify(appointmentMapper).updateEntityFromRequest(updateRequest, appointmentEntity);
    }

    @Test
    public void updateAppointment_NotFound() {
        when(appointmentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.updateAppointmentById(10L, updateRequest));
    }

    @Test
    public void deleteAppointment_Success() {
        when(appointmentRepository.existsById(10L)).thenReturn(true);
        doNothing().when(appointmentRepository).deleteById(10L);
        appointmentService.deleteAppointmentById(10L);
        verify(appointmentRepository).deleteById(10L);
    }

    @Test
    public void deleteAppointment_NotFound() {
        when(appointmentRepository.existsById(10L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.deleteAppointmentById(10L));
        verify(appointmentRepository, never()).deleteById(anyLong());
    }
}
