package com.clinix.forge.treatment;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.treatment.dto.CreateTreatmentCategoryRequest;
import com.clinix.forge.treatment.dto.TreatmentCategoryResponse;
import com.clinix.forge.treatment.dto.UpdateTreatmentCategoryRequest;
import com.clinix.forge.treatment.entity.TreatmentCategoryEntity;
import com.clinix.forge.treatment.mapper.TreatmentCategoryMapper;
import com.clinix.forge.treatment.repository.TreatmentCategoryRepository;
import com.clinix.forge.treatment.service.TreatmentCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TreatmentCategoryServiceTests {

    @Mock
    private TreatmentCategoryRepository treatmentCategoryRepository;

    @Mock
    private TreatmentCategoryMapper treatmentCategoryMapper;

    @InjectMocks
    private TreatmentCategoryService treatmentCategoryService;

    private TreatmentCategoryEntity parentEntity;
    private TreatmentCategoryEntity categoryEntity;
    private TreatmentCategoryResponse categoryResponse;
    private CreateTreatmentCategoryRequest createRequest;
    private UpdateTreatmentCategoryRequest updateRequest;

    @BeforeEach
    public void setUp() {
        parentEntity = TreatmentCategoryEntity.builder()
                .name("Surgery")
                .build();
        parentEntity.setId(1L);

        categoryEntity = TreatmentCategoryEntity.builder()
                .name("Dental Surgery")
                .parent(parentEntity)
                .build();
        categoryEntity.setId(2L);

        categoryResponse = new TreatmentCategoryResponse(2L, "Dental Surgery", 1L, Instant.now(), Instant.now());
        createRequest = new CreateTreatmentCategoryRequest("Dental Surgery", 1L);
        updateRequest = new UpdateTreatmentCategoryRequest("Dental Surgery Updated", 1L);
    }

    @Test
    public void createCategory_Success() {
        when(treatmentCategoryRepository.findById(1L)).thenReturn(Optional.of(parentEntity));
        when(treatmentCategoryRepository.findByNameAndParentId("Dental Surgery", 1L)).thenReturn(Optional.empty());
        when(treatmentCategoryMapper.toTreatmentCategoryEntity(createRequest)).thenReturn(categoryEntity);
        when(treatmentCategoryRepository.save(any(TreatmentCategoryEntity.class))).thenReturn(categoryEntity);
        when(treatmentCategoryMapper.toTreatmentCategoryResponse(categoryEntity)).thenReturn(categoryResponse);

        TreatmentCategoryResponse result = invokeCreateCategory(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        verify(treatmentCategoryRepository).save(any(TreatmentCategoryEntity.class));
    }

    @Test
    public void createCategory_ParentNotFound() {
        when(treatmentCategoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> invokeCreateCategory(createRequest));
    }

    @Test
    public void createCategory_DuplicateName() {
        when(treatmentCategoryRepository.findById(1L)).thenReturn(Optional.of(parentEntity));
        when(treatmentCategoryRepository.findByNameAndParentId("Dental Surgery", 1L)).thenReturn(Optional.of(categoryEntity));

        assertThrows(DuplicateResourceException.class, () -> invokeCreateCategory(createRequest));
    }

    @Test
    public void getCategoryById_Success() {
        when(treatmentCategoryRepository.findById(2L)).thenReturn(Optional.of(categoryEntity));
        when(treatmentCategoryMapper.toTreatmentCategoryResponse(categoryEntity)).thenReturn(categoryResponse);

        TreatmentCategoryResponse result = invokeGetCategoryById();
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
    }

    @Test
    public void getCategoryById_NotFound() {
        when(treatmentCategoryRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, this::invokeGetCategoryById);
    }

    @Test
    public void getAllCategories_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<TreatmentCategoryEntity> page = new PageImpl<>(List.of(categoryEntity));
        when(treatmentCategoryRepository.findAll(pageRequest)).thenReturn(page);
        when(treatmentCategoryMapper.toTreatmentCategoryResponse(categoryEntity)).thenReturn(categoryResponse);

        PaginatedPayload<TreatmentCategoryResponse> result = invokeGetAllCategories();
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updateCategory_Success() {
        when(treatmentCategoryRepository.findById(2L)).thenReturn(Optional.of(categoryEntity));
        when(treatmentCategoryRepository.findById(1L)).thenReturn(Optional.of(parentEntity));
        when(treatmentCategoryRepository.findByNameAndParentId("Dental Surgery Updated", 1L)).thenReturn(Optional.empty());
        when(treatmentCategoryRepository.save(categoryEntity)).thenReturn(categoryEntity);
        when(treatmentCategoryMapper.toTreatmentCategoryResponse(categoryEntity)).thenReturn(categoryResponse);

        TreatmentCategoryResponse result = invokeUpdateCategoryById();
        assertThat(result).isNotNull();
        verify(treatmentCategoryRepository).save(categoryEntity);
    }

    @Test
    public void deleteCategory_Success() {
        when(treatmentCategoryRepository.existsById(2L)).thenReturn(true);
        doNothing().when(treatmentCategoryRepository).deleteById(2L);

        invokeDeleteCategoryById();
        verify(treatmentCategoryRepository).deleteById(2L);
    }

    @Test
    public void deleteCategory_NotFound() {
        when(treatmentCategoryRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, this::invokeDeleteCategoryById);
    }

    private TreatmentCategoryResponse invokeCreateCategory(CreateTreatmentCategoryRequest request) {
        return invokeMatchingMethod(TreatmentCategoryResponse.class, new Class<?>[]{CreateTreatmentCategoryRequest.class}, request);
    }

    private TreatmentCategoryResponse invokeGetCategoryById() {
        return invokeMatchingMethod(TreatmentCategoryResponse.class, new Class<?>[]{Long.class}, 2L);
    }

    @SuppressWarnings("unchecked")
    private PaginatedPayload<TreatmentCategoryResponse> invokeGetAllCategories() {
        return (PaginatedPayload<TreatmentCategoryResponse>) invokeMatchingMethod(PaginatedPayload.class, new Class<?>[]{int.class, int.class}, 0, 10);
    }

    private TreatmentCategoryResponse invokeUpdateCategoryById() {
        return invokeMatchingMethod(TreatmentCategoryResponse.class, new Class<?>[]{Long.class, UpdateTreatmentCategoryRequest.class}, 2L, updateRequest);
    }

    private void invokeDeleteCategoryById() {
        invokeMatchingMethod(Void.class, new Class<?>[]{Long.class}, 2L);
    }

    @SuppressWarnings("unchecked")
    private <T> T invokeMatchingMethod(Class<T> returnType, Class<?>[] parameterTypes, Object... args) {
        try {
            Method method = findMatchingMethod(returnType, parameterTypes);
            return (T) method.invoke(treatmentCategoryService, args);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to invoke treatment category service method", e);
        }
    }


    private Method findMatchingMethod(Class<?> returnType, Class<?>[] parameterTypes) {
        for (Method method : treatmentCategoryService.getClass().getMethods()) {
            if (method.getParameterCount() != parameterTypes.length) {
                continue;
            }
            if (!matchesParameters(method.getParameterTypes(), parameterTypes)) {
                continue;
            }
            if (returnType == Void.class) {
                if (method.getReturnType().equals(void.class)) {
                    return method;
                }
                continue;
            }
            if (returnType.isAssignableFrom(method.getReturnType())) {
                return method;
            }
        }
        throw new IllegalStateException("No matching service method found for parameters: " + List.of(parameterTypes));
    }

    private boolean matchesParameters(Class<?>[] actual, Class<?>[] expected) {
        for (int i = 0; i < actual.length; i++) {
            Class<?> actualType = wrap(actual[i]);
            Class<?> expectedType = wrap(expected[i]);
            if (!actualType.isAssignableFrom(expectedType) && !expectedType.isAssignableFrom(actualType)) {
                return false;
            }
        }
        return true;
    }

    private Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == boolean.class) return Boolean.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == char.class) return Character.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        return type;
    }
}
