package com.clinix.forge.prescription;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.prescription.dto.CreateMedicineRequest;
import com.clinix.forge.prescription.dto.MedicineResponse;
import com.clinix.forge.prescription.dto.UpdateMedicineRequest;
import com.clinix.forge.prescription.entity.MedicineEntity;
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
public class MedicineServiceTests {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private MedicineService medicineService;

    private MedicineEntity medicineEntity;
    private MedicineResponse medicineResponse;
    private CreateMedicineRequest createRequest;
    private UpdateMedicineRequest updateRequest;

    @BeforeEach
    public void setUp() {
        medicineEntity = MedicineEntity.builder()
                .name("Paracetamol")
                .type("Tablet")
                .instruction("Take with water")
                .build();
        medicineEntity.setId(1L);

        medicineResponse = new MedicineResponse(1L, "Paracetamol", "Tablet", "Take with water", null, null);
        createRequest = new CreateMedicineRequest("Paracetamol", "Tablet", "Take with water");
        updateRequest = new UpdateMedicineRequest("Amoxicillin", "Capsule", "Take after food");
    }

    @Test
    public void createMedicine_Success() {
        when(medicineRepository.findByNameAndType("Paracetamol", "Tablet")).thenReturn(Optional.empty());
        when(prescriptionMapper.toMedicineEntity(createRequest)).thenReturn(medicineEntity);
        when(medicineRepository.save(any(MedicineEntity.class))).thenReturn(medicineEntity);
        when(prescriptionMapper.toMedicineResponse(medicineEntity)).thenReturn(medicineResponse);

        MedicineResponse result = medicineService.createMedicine(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(medicineRepository).save(any(MedicineEntity.class));
    }

    @Test
    public void createMedicine_Duplicate_ThrowsException() {
        when(medicineRepository.findByNameAndType("Paracetamol", "Tablet")).thenReturn(Optional.of(medicineEntity));
        assertThrows(DuplicateResourceException.class, () -> medicineService.createMedicine(createRequest));
    }

    @Test
    public void getMedicineById_Success() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicineEntity));
        when(prescriptionMapper.toMedicineResponse(medicineEntity)).thenReturn(medicineResponse);

        MedicineResponse result = medicineService.getMedicineById(1L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    public void getMedicineById_NotFound() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> medicineService.getMedicineById(1L));
    }

    @Test
    public void getAllMedicines_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<MedicineEntity> page = new PageImpl<>(List.of(medicineEntity));
        when(medicineRepository.findAll(pageRequest)).thenReturn(page);
        when(prescriptionMapper.toMedicineResponse(medicineEntity)).thenReturn(medicineResponse);

        PaginatedPayload<MedicineResponse> result = medicineService.getAllMedicines(0, 10);
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updateMedicine_Success() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicineEntity));
        when(medicineRepository.findByNameAndType("Amoxicillin", "Capsule")).thenReturn(Optional.empty());
        doNothing().when(prescriptionMapper).updateMedicineFromRequest(updateRequest, medicineEntity);
        when(medicineRepository.save(medicineEntity)).thenReturn(medicineEntity);
        when(prescriptionMapper.toMedicineResponse(medicineEntity)).thenReturn(medicineResponse);

        MedicineResponse result = medicineService.updateMedicineById(1L, updateRequest);
        assertThat(result).isNotNull();
        verify(medicineRepository).save(medicineEntity);
    }

    @Test
    public void deleteMedicine_Success() {
        when(medicineRepository.existsById(1L)).thenReturn(true);
        doNothing().when(medicineRepository).deleteById(1L);

        medicineService.deleteMedicineById(1L);
        verify(medicineRepository).deleteById(1L);
    }

    @Test
    public void deleteMedicine_NotFound() {
        when(medicineRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> medicineService.deleteMedicineById(1L));
    }
}
