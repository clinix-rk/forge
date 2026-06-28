package com.clinix.forge.storage;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.storage.dto.CreateFileRequest;
import com.clinix.forge.storage.dto.FileResponse;
import com.clinix.forge.storage.dto.UpdateFileRequest;
import com.clinix.forge.storage.entity.FileEntity;
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
public class FileServiceTests {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private FileService fileService;

    private PatientEntity patient;
    private FileEntity fileEntity;
    private FileResponse fileResponse;
    private CreateFileRequest createRequest;
    private UpdateFileRequest updateRequest;

    @BeforeEach
    public void setUp() {
        patient = PatientEntity.builder().name("John Doe").build();
        patient.setId(1L);

        fileEntity = FileEntity.builder()
                .patient(patient)
                .name("xray.png")
                .location("/uploads/xray.png")
                .build();
        fileEntity.setId(1L);

        fileResponse = new FileResponse(1L, 1L, "xray.png", "/uploads/xray.png", Instant.now(), Instant.now());
        createRequest = new CreateFileRequest(1L, "xray.png", "/uploads/xray.png");
        updateRequest = new UpdateFileRequest("xray_v2.png", "/uploads/xray_v2.png");
    }

    @Test
    public void createFile_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(fileRepository.findByPatientId(1L)).thenReturn(Optional.empty());
        when(fileRepository.findByName("xray.png")).thenReturn(Optional.empty());
        when(fileRepository.findByLocation("/uploads/xray.png")).thenReturn(Optional.empty());
        when(fileMapper.toEntity(createRequest)).thenReturn(fileEntity);
        when(fileRepository.save(any(FileEntity.class))).thenReturn(fileEntity);
        when(fileMapper.toResponse(fileEntity)).thenReturn(fileResponse);

        FileResponse result = fileService.createFile(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("xray.png");
        verify(fileRepository).save(any(FileEntity.class));
    }

    @Test
    public void createFile_PatientNotFound_ThrowsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fileService.createFile(createRequest));
        verify(fileRepository, never()).save(any(FileEntity.class));
    }

    @Test
    public void createFile_DuplicatePatient_ThrowsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(fileRepository.findByPatientId(1L)).thenReturn(Optional.of(fileEntity));

        assertThrows(DuplicateResourceException.class, () -> fileService.createFile(createRequest));
        verify(fileRepository, never()).save(any(FileEntity.class));
    }

    @Test
    public void createFile_DuplicateName_ThrowsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(fileRepository.findByPatientId(1L)).thenReturn(Optional.empty());
        when(fileRepository.findByName("xray.png")).thenReturn(Optional.of(fileEntity));

        assertThrows(DuplicateResourceException.class, () -> fileService.createFile(createRequest));
        verify(fileRepository, never()).save(any(FileEntity.class));
    }

    @Test
    public void getFileById_Success() {
        when(fileRepository.findById(1L)).thenReturn(Optional.of(fileEntity));
        when(fileMapper.toResponse(fileEntity)).thenReturn(fileResponse);

        FileResponse result = fileService.getFileById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    public void getFileById_NotFound_ThrowsException() {
        when(fileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fileService.getFileById(1L));
    }

    @Test
    public void getAllFiles_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<FileEntity> page = new PageImpl<>(List.of(fileEntity));
        when(fileRepository.findAll(pageRequest)).thenReturn(page);
        when(fileMapper.toResponse(fileEntity)).thenReturn(fileResponse);

        PaginatedPayload<FileResponse> result = fileService.getAllFiles(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    public void updateFile_Success() {
        when(fileRepository.findById(1L)).thenReturn(Optional.of(fileEntity));
        when(fileRepository.findByName("xray_v2.png")).thenReturn(Optional.empty());
        when(fileRepository.findByLocation("/uploads/xray_v2.png")).thenReturn(Optional.empty());
        
        doAnswer(invocation -> {
            FileEntity entity = invocation.getArgument(1);
            entity.setName("xray_v2.png");
            entity.setLocation("/uploads/xray_v2.png");
            return null;
        }).when(fileMapper).updateEntityFromRequest(eq(updateRequest), any(FileEntity.class));
        when(fileRepository.save(fileEntity)).thenReturn(fileEntity);

        FileResponse updatedResponse = new FileResponse(1L, 1L, "xray_v2.png", "/uploads/xray_v2.png", Instant.now(), Instant.now());
        when(fileMapper.toResponse(fileEntity)).thenReturn(updatedResponse);

        FileResponse result = fileService.updateFileById(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("xray_v2.png");
        assertThat(result.location()).isEqualTo("/uploads/xray_v2.png");
        verify(fileRepository).save(fileEntity);
    }

    @Test
    public void deleteFile_Success() {
        when(fileRepository.existsById(1L)).thenReturn(true);
        doNothing().when(fileRepository).deleteById(1L);

        fileService.deleteFileById(1L);

        verify(fileRepository).deleteById(1L);
    }

    @Test
    public void deleteFile_NotFound_ThrowsException() {
        when(fileRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> fileService.deleteFileById(1L));
        verify(fileRepository, never()).deleteById(1L);
    }
}
