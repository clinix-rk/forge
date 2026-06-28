package com.clinix.forge.suggestion;

import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.suggestion.dto.CreateSuggestionRequest;
import com.clinix.forge.suggestion.dto.SuggestionResponse;
import com.clinix.forge.suggestion.dto.UpdateSuggestionRequest;
import com.clinix.forge.suggestion.entity.SuggestionEntity;
import com.clinix.forge.suggestion.entity.SuggestionStatus;
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
public class SuggestionServiceTests {

    @Mock
    private SuggestionRepository suggestionRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private SuggestionMapper suggestionMapper;

    @InjectMocks
    private SuggestionService suggestionService;

    private PatientEntity patient;
    private SuggestionEntity suggestionEntity;
    private SuggestionResponse suggestionResponse;
    private CreateSuggestionRequest createRequest;
    private UpdateSuggestionRequest updateRequest;

    @BeforeEach
    public void setUp() {
        patient = PatientEntity.builder().name("John Doe").build();
        patient.setId(1L);

        suggestionEntity = SuggestionEntity.builder()
                .date(LocalDate.of(2026, 6, 18))
                .category("Diet Plan")
                .details("Less sugar")
                .cost(100)
                .status(SuggestionStatus.SUGGESTED)
                .patient(patient)
                .build();
        suggestionEntity.setId(1L);

        suggestionResponse = new SuggestionResponse(
                1L, LocalDate.of(2026, 6, 18), "Diet Plan", "Less sugar",
                100, SuggestionStatus.SUGGESTED, 1L, Instant.now(), Instant.now()
        );

        createRequest = new CreateSuggestionRequest(
                LocalDate.of(2026, 6, 18), "Diet Plan", "Less sugar",
                100, SuggestionStatus.SUGGESTED, 1L
        );

        updateRequest = new UpdateSuggestionRequest(
                LocalDate.of(2026, 6, 18), "Diet Plan", "No sugar",
                120, SuggestionStatus.ACCEPTED
        );
    }

    @Test
    public void createSuggestion_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(suggestionMapper.toEntity(createRequest)).thenReturn(suggestionEntity);
        when(suggestionRepository.save(any(SuggestionEntity.class))).thenReturn(suggestionEntity);
        when(suggestionMapper.toResponse(suggestionEntity)).thenReturn(suggestionResponse);

        SuggestionResponse result = suggestionService.createSuggestion(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.category()).isEqualTo("Diet Plan");
        verify(suggestionRepository).save(any(SuggestionEntity.class));
    }

    @Test
    public void createSuggestion_PatientNotFound_ThrowsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> suggestionService.createSuggestion(createRequest));
        verify(suggestionRepository, never()).save(any(SuggestionEntity.class));
    }

    @Test
    public void getSuggestionById_Success() {
        when(suggestionRepository.findById(1L)).thenReturn(Optional.of(suggestionEntity));
        when(suggestionMapper.toResponse(suggestionEntity)).thenReturn(suggestionResponse);

        SuggestionResponse result = suggestionService.getSuggestionById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    public void getSuggestionById_NotFound_ThrowsException() {
        when(suggestionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> suggestionService.getSuggestionById(1L));
    }

    @Test
    public void getAllSuggestions_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<SuggestionEntity> page = new PageImpl<>(List.of(suggestionEntity));
        when(suggestionRepository.findAll(pageRequest)).thenReturn(page);
        when(suggestionMapper.toResponse(suggestionEntity)).thenReturn(suggestionResponse);

        PaginatedPayload<SuggestionResponse> result = suggestionService.getAllSuggestions(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updateSuggestion_Success() {
        when(suggestionRepository.findById(1L)).thenReturn(Optional.of(suggestionEntity));
        doAnswer(invocation -> {
            SuggestionEntity entity = invocation.getArgument(1);
            entity.setDetails("No sugar");
            entity.setCost(120);
            entity.setStatus(SuggestionStatus.ACCEPTED);
            return null;
        }).when(suggestionMapper).updateEntityFromRequest(eq(updateRequest), any(SuggestionEntity.class));
        when(suggestionRepository.save(suggestionEntity)).thenReturn(suggestionEntity);
        
        SuggestionResponse updatedResponse = new SuggestionResponse(
                1L, LocalDate.of(2026, 6, 18), "Diet Plan", "No sugar",
                120, SuggestionStatus.ACCEPTED, 1L, Instant.now(), Instant.now()
        );
        when(suggestionMapper.toResponse(suggestionEntity)).thenReturn(updatedResponse);

        SuggestionResponse result = suggestionService.updateSuggestionById(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.details()).isEqualTo("No sugar");
        assertThat(result.cost()).isEqualTo(120);
        assertThat(result.status()).isEqualTo(SuggestionStatus.ACCEPTED);
        verify(suggestionRepository).save(suggestionEntity);
    }

    @Test
    public void deleteSuggestion_Success() {
        when(suggestionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(suggestionRepository).deleteById(1L);

        suggestionService.deleteSuggestionById(1L);

        verify(suggestionRepository).deleteById(1L);
    }

    @Test
    public void deleteSuggestion_NotFound_ThrowsException() {
        when(suggestionRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> suggestionService.deleteSuggestionById(1L));
        verify(suggestionRepository, never()).deleteById(1L);
    }
}
