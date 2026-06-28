package com.clinix.forge.prescription;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.prescription.dto.CreateDrugDosageRequest;
import com.clinix.forge.prescription.dto.DrugDosageResponse;
import com.clinix.forge.prescription.dto.UpdateDrugDosageRequest;
import com.clinix.forge.prescription.entity.DrugDosageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DrugDosageServiceTests {

    @Mock
    private DrugDosageRepository drugDosageRepository;

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private DrugDosageService drugDosageService;

    private DrugDosageEntity dosageEntity;
    private DrugDosageResponse dosageResponse;
    private CreateDrugDosageRequest createRequest;
    private UpdateDrugDosageRequest updateRequest;

    @BeforeEach
    public void setUp() {
        dosageEntity = DrugDosageEntity.builder()
                .dosage("1-0-1")
                .build();
        dosageEntity.setId(1L);

        dosageResponse = new DrugDosageResponse(1L, "1-0-1", null, null);
        createRequest = new CreateDrugDosageRequest("1-0-1");
        updateRequest = new UpdateDrugDosageRequest("1-1-1");
    }

    @Test
    public void createDrugDosage_Success() {
        when(drugDosageRepository.findByDosage("1-0-1")).thenReturn(Optional.empty());
        when(prescriptionMapper.toDosageEntity(createRequest)).thenReturn(dosageEntity);
        when(drugDosageRepository.save(any(DrugDosageEntity.class))).thenReturn(dosageEntity);
        when(prescriptionMapper.toDosageResponse(dosageEntity)).thenReturn(dosageResponse);

        DrugDosageResponse result = drugDosageService.createDrugDosage(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(drugDosageRepository).save(any(DrugDosageEntity.class));
    }

    @Test
    public void createDrugDosage_Duplicate_ThrowsException() {
        when(drugDosageRepository.findByDosage("1-0-1")).thenReturn(Optional.of(dosageEntity));
        assertThrows(DuplicateResourceException.class, () -> drugDosageService.createDrugDosage(createRequest));
    }

    @Test
    public void getDrugDosageById_Success() {
        when(drugDosageRepository.findById(1L)).thenReturn(Optional.of(dosageEntity));
        when(prescriptionMapper.toDosageResponse(dosageEntity)).thenReturn(dosageResponse);

        DrugDosageResponse result = drugDosageService.getDrugDosageById(1L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    public void getDrugDosageById_NotFound() {
        when(drugDosageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> drugDosageService.getDrugDosageById(1L));
    }

    @Test
    public void getAllDrugDosages_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<DrugDosageEntity> page = new PageImpl<>(List.of(dosageEntity));
        when(drugDosageRepository.findAll(pageRequest)).thenReturn(page);
        when(prescriptionMapper.toDosageResponse(dosageEntity)).thenReturn(dosageResponse);

        PaginatedPayload<DrugDosageResponse> result = drugDosageService.getAllDrugDosages(0, 10);
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updateDrugDosage_Success() {
        when(drugDosageRepository.findById(1L)).thenReturn(Optional.of(dosageEntity));
        when(drugDosageRepository.findByDosage("1-1-1")).thenReturn(Optional.empty());
        doNothing().when(prescriptionMapper).updateDosageFromRequest(updateRequest, dosageEntity);
        when(drugDosageRepository.save(dosageEntity)).thenReturn(dosageEntity);
        when(prescriptionMapper.toDosageResponse(dosageEntity)).thenReturn(dosageResponse);

        DrugDosageResponse result = drugDosageService.updateDrugDosageById(1L, updateRequest);
        assertThat(result).isNotNull();
        verify(drugDosageRepository).save(dosageEntity);
    }

    @Test
    public void deleteDrugDosage_Success() {
        when(drugDosageRepository.existsById(1L)).thenReturn(true);
        doNothing().when(drugDosageRepository).deleteById(1L);

        drugDosageService.deleteDrugDosageById(1L);
        verify(drugDosageRepository).deleteById(1L);
    }

    @Test
    public void deleteDrugDosage_NotFound() {
        when(drugDosageRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> drugDosageService.deleteDrugDosageById(1L));
    }
}
