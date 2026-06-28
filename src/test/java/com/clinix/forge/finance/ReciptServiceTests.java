package com.clinix.forge.finance;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.finance.dto.CreateReciptRequest;
import com.clinix.forge.finance.dto.ReciptResponse;
import com.clinix.forge.finance.dto.UpdateReciptRequest;
import com.clinix.forge.finance.entity.ReciptEntity;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReciptServiceTests {

    @Mock
    private ReciptRepository reciptRepository;

    @Mock
    private ReciptMapper reciptMapper;

    @InjectMocks
    private ReciptService reciptService;

    private ReciptEntity reciptEntity;
    private ReciptResponse reciptResponse;
    private CreateReciptRequest createRequest;
    private UpdateReciptRequest updateRequest;

    @BeforeEach
    public void setUp() {
        reciptEntity = ReciptEntity.builder()
                .doctorIdentityCharacter("D")
                .financialYear("2023-2024")
                .serial(1001)
                .build();
        reciptEntity.setId(10L);

        reciptResponse = new ReciptResponse(10L, "D", "2023-2024", 1001, Instant.now(), Instant.now());
        createRequest = new CreateReciptRequest("D", "2023-2024", 1001);
        updateRequest = new UpdateReciptRequest("D", "2023-2024", 1002);
    }

    @Test
    public void createRecipt_Success() {
        when(reciptRepository.findByFinancialYearAndDoctorIdentityCharacterAndSerial("2023-2024", "D", 1001))
                .thenReturn(Optional.empty());
        when(reciptMapper.toEntity(createRequest)).thenReturn(reciptEntity);
        when(reciptRepository.save(any(ReciptEntity.class))).thenReturn(reciptEntity);
        when(reciptMapper.toResponse(reciptEntity)).thenReturn(reciptResponse);

        ReciptResponse result = reciptService.createRecipt(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        verify(reciptRepository).save(any(ReciptEntity.class));
    }

    @Test
    public void createRecipt_Duplicate_ThrowsException() {
        when(reciptRepository.findByFinancialYearAndDoctorIdentityCharacterAndSerial("2023-2024", "D", 1001))
                .thenReturn(Optional.of(reciptEntity));

        assertThrows(DuplicateResourceException.class, () -> reciptService.createRecipt(createRequest));
    }

    @Test
    public void getReciptById_Success() {
        when(reciptRepository.findById(10L)).thenReturn(Optional.of(reciptEntity));
        when(reciptMapper.toResponse(reciptEntity)).thenReturn(reciptResponse);

        ReciptResponse result = reciptService.getReciptById(10L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    public void getReciptById_NotFound() {
        when(reciptRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reciptService.getReciptById(10L));
    }

    @Test
    public void getAllRecipts_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ReciptEntity> page = new PageImpl<>(List.of(reciptEntity));
        when(reciptRepository.findAll(pageRequest)).thenReturn(page);
        when(reciptMapper.toResponse(reciptEntity)).thenReturn(reciptResponse);

        PaginatedPayload<ReciptResponse> result = reciptService.getAllRecipts(0, 10);
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updateRecipt_Success() {
        when(reciptRepository.findById(10L)).thenReturn(Optional.of(reciptEntity));
        when(reciptRepository.findByFinancialYearAndDoctorIdentityCharacterAndSerial("2023-2024", "D", 1002))
                .thenReturn(Optional.empty());
        doNothing().when(reciptMapper).updateEntityFromRequest(updateRequest, reciptEntity);
        when(reciptRepository.save(reciptEntity)).thenReturn(reciptEntity);
        when(reciptMapper.toResponse(reciptEntity)).thenReturn(reciptResponse);

        ReciptResponse result = reciptService.updateReciptById(10L, updateRequest);
        assertThat(result).isNotNull();
        verify(reciptRepository).save(reciptEntity);
    }

    @Test
    public void deleteRecipt_Success() {
        when(reciptRepository.existsById(10L)).thenReturn(true);
        doNothing().when(reciptRepository).deleteById(10L);

        reciptService.deleteReciptById(10L);
        verify(reciptRepository).deleteById(10L);
    }

    @Test
    public void deleteRecipt_NotFound() {
        when(reciptRepository.existsById(10L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> reciptService.deleteReciptById(10L));
    }
}
