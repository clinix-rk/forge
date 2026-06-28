package com.clinix.forge.treatment;

import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.treatment.dto.CreateTreatmentRequest;
import com.clinix.forge.treatment.dto.TreatmentResponse;
import com.clinix.forge.treatment.dto.UpdateTreatmentRequest;
import com.clinix.forge.treatment.entity.TreatmentCategoryEntity;
import com.clinix.forge.treatment.entity.TreatmentEntity;
import com.clinix.forge.finance.ReciptRepository;
import com.clinix.forge.finance.PaymentRepository;
import com.clinix.forge.finance.entity.ReciptEntity;
import com.clinix.forge.finance.entity.PaymentEntity;
import com.clinix.forge.doctors.DoctorEntity;
import com.clinix.forge.treatment.mapper.TreatmentMapper;
import com.clinix.forge.treatment.repository.TreatmentCategoryRepository;
import com.clinix.forge.treatment.repository.TreatmentRepository;
import com.clinix.forge.treatment.service.TreatmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TreatmentServiceTests {

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private TreatmentCategoryRepository treatmentCategoryRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private TreatmentMapper treatmentMapper;

    @Mock
    private ReciptRepository reciptRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private TreatmentService treatmentService;

    private PatientEntity patientEntity;
    private TreatmentCategoryEntity categoryEntity;
    private TreatmentEntity treatmentEntity;
    private TreatmentResponse treatmentResponse;
    private CreateTreatmentRequest createRequest;
    private UpdateTreatmentRequest updateRequest;

    @BeforeEach
    public void setUp() {
        DoctorEntity doctor = DoctorEntity.builder().caseNoPrefix("Y").name("Dr. Yogesh").build();
        patientEntity = PatientEntity.builder().doctor(doctor).build();
        patientEntity.setId(1L);

        categoryEntity = TreatmentCategoryEntity.builder().name("Root Canal").build();
        categoryEntity.setId(2L);

        treatmentEntity = TreatmentEntity.builder()
                .date(LocalDate.of(2023, 1, 1))
                .details("Dental cleaning and root canal therapy")
                .patient(patientEntity)
                .category(categoryEntity)
                .build();
        treatmentEntity.setId(10L);

        treatmentResponse = new TreatmentResponse(10L, "Dental cleaning and root canal therapy", LocalDate.of(2023, 1, 1), 2L, 1L, Instant.now(), Instant.now());
        createRequest = new CreateTreatmentRequest("Dental cleaning and root canal therapy", LocalDate.of(2023, 1, 1), 2L, 1L);
        updateRequest = new UpdateTreatmentRequest("Updated treatment details", LocalDate.of(2023, 1, 2), 2L);
    }

    @Test
    public void createTreatment_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientEntity));
        when(treatmentCategoryRepository.findById(2L)).thenReturn(Optional.of(categoryEntity));
        when(treatmentMapper.toEntity(createRequest)).thenReturn(treatmentEntity);
        when(treatmentRepository.save(any(TreatmentEntity.class))).thenReturn(treatmentEntity);
        when(reciptRepository.findMaxSerialByFinancialYearAndDoctorIdentityCharacter(anyString(), anyString())).thenReturn(5);
        when(reciptRepository.save(any(ReciptEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(treatmentMapper.toResponse(treatmentEntity)).thenReturn(treatmentResponse);

        TreatmentResponse result = treatmentService.createTreatment(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        verify(treatmentRepository).save(any(TreatmentEntity.class));
        verify(reciptRepository).save(any(ReciptEntity.class));
        verify(paymentRepository).save(any(PaymentEntity.class));
    }

    @Test
    public void createTreatment_PatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> treatmentService.createTreatment(createRequest));
    }

    @Test
    public void createTreatment_CategoryNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientEntity));
        when(treatmentCategoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> treatmentService.createTreatment(createRequest));
    }

    @Test
    public void getTreatmentById_Success() {
        when(treatmentRepository.findById(10L)).thenReturn(Optional.of(treatmentEntity));
        when(treatmentMapper.toResponse(treatmentEntity)).thenReturn(treatmentResponse);

        TreatmentResponse result = treatmentService.getTreatmentById(10L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    public void getTreatmentById_NotFound() {
        when(treatmentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> treatmentService.getTreatmentById(10L));
    }

    @Test
    public void getAllTreatments_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<TreatmentEntity> page = new PageImpl<>(List.of(treatmentEntity));
        when(treatmentRepository.findAll(pageRequest)).thenReturn(page);
        when(treatmentMapper.toResponse(treatmentEntity)).thenReturn(treatmentResponse);

        PaginatedPayload<TreatmentResponse> result = treatmentService.getAllTreatments(0, 10);
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updateTreatment_Success() {
        when(treatmentRepository.findById(10L)).thenReturn(Optional.of(treatmentEntity));
        when(treatmentCategoryRepository.findById(2L)).thenReturn(Optional.of(categoryEntity));
        doNothing().when(treatmentMapper).updateEntityFromRequest(updateRequest, treatmentEntity);
        when(treatmentRepository.save(treatmentEntity)).thenReturn(treatmentEntity);
        when(treatmentMapper.toResponse(treatmentEntity)).thenReturn(treatmentResponse);

        TreatmentResponse result = treatmentService.updateTreatmentById(10L, updateRequest);
        assertThat(result).isNotNull();
        verify(treatmentRepository).save(treatmentEntity);
    }

    @Test
    public void deleteTreatment_Success() {
        when(treatmentRepository.existsById(10L)).thenReturn(true);
        doNothing().when(treatmentRepository).deleteById(10L);

        treatmentService.deleteTreatmentById(10L);
        verify(treatmentRepository).deleteById(10L);
    }

    @Test
    public void deleteTreatment_NotFound() {
        when(treatmentRepository.existsById(10L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> treatmentService.deleteTreatmentById(10L));
    }
}
