package com.clinix.forge.prescription;

import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.prescription.dto.*;
import com.clinix.forge.prescription.entity.DrugDosageEntity;
import com.clinix.forge.prescription.entity.MedicineEntity;
import com.clinix.forge.prescription.entity.PrescriptionEntity;
import com.clinix.forge.prescription.entity.PrescriptionMedicineEntity;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrescriptionServiceTests {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private DrugDosageRepository drugDosageRepository;

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private PatientEntity patientEntity;
    private MedicineEntity medicineEntity;
    private DrugDosageEntity dosageEntity;
    private PrescriptionEntity prescriptionEntity;
    private PrescriptionResponse prescriptionResponse;
    private CreatePrescriptionRequest createRequest;
    private UpdatePrescriptionRequest updateRequest;

    @BeforeEach
    public void setUp() {
        patientEntity = PatientEntity.builder().build();
        patientEntity.setId(10L);

        medicineEntity = MedicineEntity.builder().name("Paracetamol").type("Tablet").build();
        medicineEntity.setId(20L);

        dosageEntity = DrugDosageEntity.builder().dosage("1-0-1").build();
        dosageEntity.setId(30L);

        prescriptionEntity = PrescriptionEntity.builder()
                .date(LocalDate.of(2023, 1, 1))
                .details("Seasonal allergy")
                .patient(patientEntity)
                .prescriptionMedicines(new HashSet<>())
                .build();
        prescriptionEntity.setId(100L);

        PrescriptionMedicineRequest medicineReq = new PrescriptionMedicineRequest(20L, 30L, 10);
        List<PrescriptionMedicineRequest> medsList = new ArrayList<>();
        medsList.add(medicineReq);

        createRequest = new CreatePrescriptionRequest(10L, LocalDate.of(2023, 1, 1), "Seasonal allergy", medsList);
        updateRequest = new UpdatePrescriptionRequest(LocalDate.of(2023, 1, 2), "Updated Seasonal allergy", medsList);

        prescriptionResponse = new PrescriptionResponse(100L, 10L, LocalDate.of(2023, 1, 1), "Seasonal allergy", List.of(), Instant.now(), Instant.now());
    }

    @Test
    public void createPrescription_Success() {
        when(patientRepository.findById(10L)).thenReturn(Optional.of(patientEntity));
        when(prescriptionMapper.toPrescriptionEntity(createRequest)).thenReturn(prescriptionEntity);
        when(medicineRepository.findById(20L)).thenReturn(Optional.of(medicineEntity));
        when(drugDosageRepository.findById(30L)).thenReturn(Optional.of(dosageEntity));
        when(prescriptionMapper.toPrescriptionMedicineEntity(any(PrescriptionMedicineRequest.class))).thenReturn(new PrescriptionMedicineEntity());
        when(prescriptionRepository.save(any(PrescriptionEntity.class))).thenReturn(prescriptionEntity);
        when(prescriptionMapper.toPrescriptionResponse(prescriptionEntity)).thenReturn(prescriptionResponse);

        PrescriptionResponse result = prescriptionService.createPrescription(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
        verify(prescriptionRepository).save(any(PrescriptionEntity.class));
    }

    @Test
    public void createPrescription_PatientNotFound() {
        when(patientRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.createPrescription(createRequest));
    }

    @Test
    public void createPrescription_MedicineNotFound() {
        when(patientRepository.findById(10L)).thenReturn(Optional.of(patientEntity));
        when(prescriptionMapper.toPrescriptionEntity(createRequest)).thenReturn(prescriptionEntity);
        when(medicineRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.createPrescription(createRequest));
    }

    @Test
    public void getPrescriptionById_Success() {
        when(prescriptionRepository.findById(100L)).thenReturn(Optional.of(prescriptionEntity));
        when(prescriptionMapper.toPrescriptionResponse(prescriptionEntity)).thenReturn(prescriptionResponse);

        PrescriptionResponse result = prescriptionService.getPrescriptionById(100L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
    }

    @Test
    public void getPrescriptionById_NotFound() {
        when(prescriptionRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.getPrescriptionById(100L));
    }

    @Test
    public void getAllPrescriptions_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PrescriptionEntity> page = new PageImpl<>(List.of(prescriptionEntity));
        when(prescriptionRepository.findAll(pageRequest)).thenReturn(page);
        when(prescriptionMapper.toPrescriptionResponse(prescriptionEntity)).thenReturn(prescriptionResponse);

        PaginatedPayload<PrescriptionResponse> result = prescriptionService.getAllPrescriptions(0, 10);
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updatePrescription_Success() {
        when(prescriptionRepository.findById(100L)).thenReturn(Optional.of(prescriptionEntity));
        doNothing().when(prescriptionMapper).updatePrescriptionFromRequest(updateRequest, prescriptionEntity);
        when(medicineRepository.findById(20L)).thenReturn(Optional.of(medicineEntity));
        when(drugDosageRepository.findById(30L)).thenReturn(Optional.of(dosageEntity));
        when(prescriptionMapper.toPrescriptionMedicineEntity(any(PrescriptionMedicineRequest.class))).thenReturn(new PrescriptionMedicineEntity());
        when(prescriptionRepository.save(prescriptionEntity)).thenReturn(prescriptionEntity);
        when(prescriptionMapper.toPrescriptionResponse(prescriptionEntity)).thenReturn(prescriptionResponse);

        PrescriptionResponse result = prescriptionService.updatePrescriptionById(100L, updateRequest);
        assertThat(result).isNotNull();
        verify(prescriptionRepository).save(prescriptionEntity);
    }

    @Test
    public void deletePrescription_Success() {
        when(prescriptionRepository.existsById(100L)).thenReturn(true);
        doNothing().when(prescriptionRepository).deleteById(100L);

        prescriptionService.deletePrescriptionById(100L);
        verify(prescriptionRepository).deleteById(100L);
    }

    @Test
    public void deletePrescription_NotFound() {
        when(prescriptionRepository.existsById(100L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.deletePrescriptionById(100L));
    }
}
