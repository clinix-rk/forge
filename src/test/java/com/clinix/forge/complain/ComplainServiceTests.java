package com.clinix.forge.complain;

import com.clinix.forge.complain.dto.ComplainResponse;
import com.clinix.forge.complain.dto.CreateComplainRequest;
import com.clinix.forge.complain.dto.UpdateComplainRequest;
import com.clinix.forge.complain.entity.ComplainCategoryEntity;
import com.clinix.forge.complain.entity.ComplainEntity;
import com.clinix.forge.complain.mapper.ComplainMapper;
import com.clinix.forge.complain.repository.ComplainCategoryRepository;
import com.clinix.forge.complain.repository.ComplainRepository;
import com.clinix.forge.complain.service.ComplainService;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
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
public class ComplainServiceTests {

    @Mock
    private ComplainRepository complainRepository;

    @Mock
    private ComplainCategoryRepository complainCategoryRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ComplainMapper complainMapper;

    @InjectMocks
    private ComplainService complainService;

    private PatientEntity patientEntity;
    private ComplainCategoryEntity categoryEntity;
    private ComplainEntity complainEntity;
    private ComplainResponse complainResponse;
    private CreateComplainRequest createRequest;
    private UpdateComplainRequest updateRequest;

    @BeforeEach
    public void setUp() {
        patientEntity = PatientEntity.builder().build();
        patientEntity.setId(1L);

        categoryEntity = ComplainCategoryEntity.builder().name("General").build();
        categoryEntity.setId(2L);

        complainEntity = ComplainEntity.builder()
                .date(LocalDate.of(2023, 1, 1))
                .details("Chest Pain")
                .patient(patientEntity)
                .category(categoryEntity)
                .build();
        complainEntity.setId(10L);

        complainResponse = new ComplainResponse(10L, LocalDate.of(2023, 1, 1), "Chest Pain", 2L, 1L, Instant.now(), Instant.now());
        createRequest = new CreateComplainRequest(LocalDate.of(2023, 1, 1), "Chest Pain", 2L, 1L);
        updateRequest = new UpdateComplainRequest(LocalDate.of(2023, 1, 2), "Chest Pain Updated", 2L);
    }

    @Test
    public void createComplain_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientEntity));
        when(complainCategoryRepository.findById(2L)).thenReturn(Optional.of(categoryEntity));
        when(complainMapper.toEntity(createRequest)).thenReturn(complainEntity);
        when(complainRepository.save(any(ComplainEntity.class))).thenReturn(complainEntity);
        when(complainMapper.toResponse(complainEntity)).thenReturn(complainResponse);

        ComplainResponse result = complainService.createComplain(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        verify(complainRepository).save(any(ComplainEntity.class));
    }

    @Test
    public void createComplain_PatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> complainService.createComplain(createRequest));
        verify(complainRepository, never()).save(any());
    }

    @Test
    public void createComplain_CategoryNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientEntity));
        when(complainCategoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> complainService.createComplain(createRequest));
        verify(complainRepository, never()).save(any());
    }

    @Test
    public void getComplainById_Success() {
        when(complainRepository.findById(10L)).thenReturn(Optional.of(complainEntity));
        when(complainMapper.toResponse(complainEntity)).thenReturn(complainResponse);

        ComplainResponse result = complainService.getComplainById(10L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    public void getComplainById_NotFound() {
        when(complainRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> complainService.getComplainById(10L));
    }

    @Test
    public void getAllComplains_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ComplainEntity> page = new PageImpl<>(List.of(complainEntity));
        when(complainRepository.findAll(pageRequest)).thenReturn(page);
        when(complainMapper.toResponse(complainEntity)).thenReturn(complainResponse);

        PaginatedPayload<ComplainResponse> result = complainService.getAllComplains(0, 10);
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updateComplain_Success() {
        when(complainRepository.findById(10L)).thenReturn(Optional.of(complainEntity));
        when(complainCategoryRepository.findById(2L)).thenReturn(Optional.of(categoryEntity));
        doNothing().when(complainMapper).updateEntityFromRequest(updateRequest, complainEntity);
        when(complainRepository.save(complainEntity)).thenReturn(complainEntity);
        when(complainMapper.toResponse(complainEntity)).thenReturn(complainResponse);

        ComplainResponse result = complainService.updateComplainById(10L, updateRequest);
        assertThat(result).isNotNull();
        verify(complainRepository).save(complainEntity);
    }

    @Test
    public void deleteComplain_Success() {
        when(complainRepository.existsById(10L)).thenReturn(true);
        doNothing().when(complainRepository).deleteById(10L);

        complainService.deleteComplainById(10L);
        verify(complainRepository).deleteById(10L);
    }

    @Test
    public void deleteComplain_NotFound() {
        when(complainRepository.existsById(10L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> complainService.deleteComplainById(10L));
    }
}
