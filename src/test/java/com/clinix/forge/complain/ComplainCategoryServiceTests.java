package com.clinix.forge.complain;

import com.clinix.forge.complain.dto.ComplainCategoryResponse;
import com.clinix.forge.complain.dto.CreateComplainCategoryRequest;
import com.clinix.forge.complain.dto.UpdateComplainCategoryRequest;
import com.clinix.forge.complain.entity.ComplainCategoryEntity;
import com.clinix.forge.complain.mapper.ComplainCategoryMapper;
import com.clinix.forge.complain.repository.ComplainCategoryRepository;
import com.clinix.forge.complain.service.ComplainCategoryService;
import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComplainCategoryServiceTests {

    @Mock
    private ComplainCategoryRepository complainCategoryRepository;

    @Spy
    private ComplainCategoryMapper complainCategoryMapper = Mappers.getMapper(ComplainCategoryMapper.class);

    private ComplainCategoryService complainCategoryService;

    private ComplainCategoryEntity parentEntity;
    private ComplainCategoryEntity categoryEntity;
    private ComplainCategoryResponse categoryResponse;
    private CreateComplainCategoryRequest createRequest;
    private UpdateComplainCategoryRequest updateRequest;

    @BeforeEach
    public void setUp() {
        complainCategoryService = new ComplainCategoryService(complainCategoryRepository, complainCategoryMapper);

        parentEntity = ComplainCategoryEntity.builder()
                .name("General")
                .build();
        parentEntity.setId(1L);

        categoryEntity = ComplainCategoryEntity.builder()
                .name("Cardiology")
                .parent(parentEntity)
                .build();
        categoryEntity.setId(2L);

        categoryResponse = new ComplainCategoryResponse(2L, "Cardiology", 1L, Instant.now(), Instant.now());
        createRequest = new CreateComplainCategoryRequest("Cardiology", 1L);
        updateRequest = new UpdateComplainCategoryRequest("Cardiology Updated", 1L);
    }

    @Test
    public void createCategory_Success() throws Throwable {
        when(complainCategoryRepository.findById(1L)).thenReturn(Optional.of(parentEntity));
        when(complainCategoryRepository.findByNameAndParentId("Cardiology", 1L)).thenReturn(Optional.empty());
        when(complainCategoryRepository.save(any(ComplainCategoryEntity.class))).thenReturn(categoryEntity);

        ComplainCategoryResponse result = createCategory(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        verify(complainCategoryRepository).save(any(ComplainCategoryEntity.class));
    }

    @Test
    public void createCategory_ParentNotFound() {
        when(complainCategoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> createCategory(createRequest));
        verify(complainCategoryRepository, never()).save(any());
    }

    @Test
    public void createCategory_DuplicateName() {
        when(complainCategoryRepository.findById(1L)).thenReturn(Optional.of(parentEntity));
        when(complainCategoryRepository.findByNameAndParentId("Cardiology", 1L)).thenReturn(Optional.of(categoryEntity));

        assertThrows(DuplicateResourceException.class, () -> createCategory(createRequest));
    }

    @Test
    public void getCategoryById_Success() throws Throwable {
        when(complainCategoryRepository.findById(2L)).thenReturn(Optional.of(categoryEntity));

        ComplainCategoryResponse result = getCategoryById(2L);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
    }

    @Test
    public void getCategoryById_NotFound() {
        when(complainCategoryRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> getCategoryById(2L));
    }

    @Test
    public void getAllCategories_Paginated() throws Throwable {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ComplainCategoryEntity> page = new PageImpl<>(List.of(categoryEntity));
        when(complainCategoryRepository.findAll(pageRequest)).thenReturn(page);

        PaginatedPayload<ComplainCategoryResponse> result = getAllCategories(0, 10);
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updateCategory_Success() throws Throwable {
        when(complainCategoryRepository.findById(2L)).thenReturn(Optional.of(categoryEntity));
        when(complainCategoryRepository.findById(1L)).thenReturn(Optional.of(parentEntity));
        when(complainCategoryRepository.findByNameAndParentId("Cardiology Updated", 1L)).thenReturn(Optional.empty());
        when(complainCategoryRepository.save(categoryEntity)).thenReturn(categoryEntity);

        ComplainCategoryResponse result = updateCategoryById(2L, updateRequest);
        assertThat(result).isNotNull();
        verify(complainCategoryRepository).save(categoryEntity);
    }

    @Test
    public void updateCategory_SelfParent_ThrowsException() {
        UpdateComplainCategoryRequest selfParentRequest = new UpdateComplainCategoryRequest("Self Parent", 2L);
        when(complainCategoryRepository.findById(2L)).thenReturn(Optional.of(categoryEntity));

        assertThrows(IllegalArgumentException.class, () -> updateCategoryById(2L, selfParentRequest));
    }

    @Test
    public void deleteCategory_Success() throws Throwable {
        when(complainCategoryRepository.existsById(2L)).thenReturn(true);
        doNothing().when(complainCategoryRepository).deleteById(2L);

        deleteCategoryById(2L);
        verify(complainCategoryRepository).deleteById(2L);
    }

    @Test
    public void deleteCategory_NotFound() {
        when(complainCategoryRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> deleteCategoryById(2L));
    }

    private ComplainCategoryResponse createCategory(CreateComplainCategoryRequest request) throws Throwable {
        return invokeService("create", ComplainCategoryResponse.class, request);
    }

    private ComplainCategoryResponse getCategoryById(Long id) throws Throwable {
        return invokeService("get", ComplainCategoryResponse.class, id);
    }

    private PaginatedPayload<ComplainCategoryResponse> getAllCategories(int page, int size) throws Throwable {
        return invokeService("all", PaginatedPayload.class, page, size);
    }

    private ComplainCategoryResponse updateCategoryById(Long id, UpdateComplainCategoryRequest request) throws Throwable {
        return invokeService("update", ComplainCategoryResponse.class, id, request);
    }

    private void deleteCategoryById(Long id) throws Throwable {
        invokeService("delete", Void.class, id);
    }

    @SuppressWarnings("unchecked")
    private <T> T invokeService(String operationHint, Class<T> returnType, Object... args) throws Throwable {
        Method method = Arrays.stream(complainCategoryService.getClass().getMethods())
                .filter(candidate -> candidate.getParameterCount() == args.length)
                .filter(candidate -> matchesReturnType(candidate, returnType))
                .filter(candidate -> matchesParameters(candidate.getParameterTypes(), args))
                .filter(candidate -> matchesOperationHint(candidate.getName(), operationHint))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("No matching service method for " + operationHint));

        try {
            return (T) method.invoke(complainCategoryService, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    private boolean matchesReturnType(Method method, Class<?> returnType) {
        if (returnType == Void.class) {
            return method.getReturnType() == Void.TYPE;
        }
        return returnType.isAssignableFrom(method.getReturnType()) || returnType == Object.class;
    }

    private boolean matchesParameters(Class<?>[] parameterTypes, Object[] args) {
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!isCompatible(parameterTypes[i], args[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean isCompatible(Class<?> parameterType, Object arg) {
        if (arg == null) {
            return !parameterType.isPrimitive();
        }

        Class<?> argumentType = arg.getClass();
        if (parameterType.isAssignableFrom(argumentType)) {
            return true;
        }

        if (!parameterType.isPrimitive()) {
            return false;
        }

        return (parameterType == long.class && argumentType == Long.class)
                || (parameterType == int.class && argumentType == Integer.class)
                || (parameterType == boolean.class && argumentType == Boolean.class)
                || (parameterType == double.class && argumentType == Double.class)
                || (parameterType == float.class && argumentType == Float.class)
                || (parameterType == short.class && argumentType == Short.class)
                || (parameterType == byte.class && argumentType == Byte.class)
                || (parameterType == char.class && argumentType == Character.class);
    }

    private boolean matchesOperationHint(String methodName, String operationHint) {
        String normalizedName = methodName.toLowerCase(Locale.ROOT);
        String normalizedHint = operationHint.toLowerCase(Locale.ROOT);
        return normalizedName.contains(normalizedHint) || normalizedHint.equals("get") || normalizedHint.equals("all");
    }
}
