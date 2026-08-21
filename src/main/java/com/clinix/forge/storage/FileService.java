package com.clinix.forge.storage;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.storage.dto.CreateFileRequest;
import com.clinix.forge.storage.dto.FileResponse;
import com.clinix.forge.storage.dto.UpdateFileRequest;
import com.clinix.forge.storage.entity.FileEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final PatientRepository patientRepository;
    private final FileMapper fileMapper;
    private final DiskStorageService diskStorageService;

    @Transactional(rollbackFor = Exception.class)
    public FileResponse createFile(CreateFileRequest request) {
        log.info("Creating file record for patient ID: {}", request.patientId());

        PatientEntity patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.patientId()));

        if (fileRepository.findByPatientId(request.patientId()).isPresent()) {
            log.warn("File already exists for patient ID: {}", request.patientId());
            throw new DuplicateResourceException("Patient already has an associated file");
        }

        if (fileRepository.findByName(request.name()).isPresent()) {
            log.warn("File name already exists: {}", request.name());
            throw new DuplicateResourceException("File with name " + request.name() + " already exists");
        }

        if (fileRepository.findByLocation(request.location()).isPresent()) {
            log.warn("File location already exists: {}", request.location());
            throw new DuplicateResourceException("File at location " + request.location() + " already exists");
        }

        FileEntity entity = fileMapper.toEntity(request);
        entity.setPatient(patient);

        FileEntity savedFile = fileRepository.save(entity);
        log.info("File record created successfully with ID: {}", savedFile.getId());
        return fileMapper.toResponse(savedFile);
    }

    @Transactional(readOnly = true)
    public Page<FileResponse> getAllFiles(int pageNo, int pageSize) {
        log.debug("Fetching files - PageNo: {}, PageSize: {}", pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<FileEntity> filePage = fileRepository.findAll(pageRequest);

        return filePage.map(fileMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FileResponse getFileById(Long id) {
        log.debug("Fetching file with ID: {}", id);
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + id));
        return fileMapper.toResponse(fileEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public FileResponse updateFileById(Long id, UpdateFileRequest request) {
        log.info("Updating file with ID: {}", id);
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + id));

        // Uniqueness checks on update (only if name/location changed)
        if (!fileEntity.getName().equals(request.name()) && fileRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateResourceException("File with name " + request.name() + " already exists");
        }

        if (!fileEntity.getLocation().equals(request.location()) && fileRepository.findByLocation(request.location()).isPresent()) {
            throw new DuplicateResourceException("File at location " + request.location() + " already exists");
        }

        fileMapper.updateEntityFromRequest(request, fileEntity);

        FileEntity updatedFile = fileRepository.save(fileEntity);
        log.info("File updated successfully with ID: {}", updatedFile.getId());
        return fileMapper.toResponse(updatedFile);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFileById(Long id) {
        log.info("Deleting file with ID: {}", id);
        if (!fileRepository.existsById(id)) {
            log.warn("File not found for deletion with ID: {}", id);
            throw new ResourceNotFoundException("File not found with ID: " + id);
        }
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + id));

        diskStorageService.delete(fileEntity.getLocation());

        fileRepository.deleteById(id);

        log.info("File deleted successfully: {}", id);
    }

    @Transactional(rollbackFor = Exception.class)
    public FileResponse uploadPatientPdf(Long patientId, MultipartFile file) {
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        java.util.Optional<FileEntity> existingOpt = fileRepository.findByPatientId(patientId);

        String location = diskStorageService.store(file, patientId);

        FileEntity entity;
        if (existingOpt.isPresent()) {
            entity = existingOpt.get();
            // If the new file has a different path, delete the old file from disk
            if (!entity.getLocation().equals(location)) {
                diskStorageService.delete(entity.getLocation());
            }
            entity.setName(file.getOriginalFilename());
            entity.setLocation(location);
        } else {
            entity = FileEntity.builder()
                    .patient(patient)
                    .name(file.getOriginalFilename())
                    .location(location)
                    .build();
        }

        // We also want to clear any uniqueness conflict on name/location in database:
        // Delete or verify if other patient matches the name/location
        java.util.Optional<FileEntity> sameNameOpt = fileRepository.findByName(entity.getName());
        if (sameNameOpt.isPresent() && !sameNameOpt.get().getPatient().getId().equals(patientId)) {
            throw new DuplicateResourceException("File with name " + entity.getName() + " already exists for another patient");
        }
        java.util.Optional<FileEntity> sameLocationOpt = fileRepository.findByLocation(entity.getLocation());
        if (sameLocationOpt.isPresent() && !sameLocationOpt.get().getPatient().getId().equals(patientId)) {
            throw new DuplicateResourceException("File at location " + entity.getLocation() + " already exists for another patient");
        }

        FileEntity saved = fileRepository.save(entity);
        log.info("File uploaded and registered with ID: {}", saved.getId());
        return fileMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public FileResponse getFileByPatientId(Long patientId) {
        log.debug("Fetching file with Patient ID: {}", patientId);
        return fileRepository.findByPatientId(patientId)
                .map(fileMapper::toResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public byte[] downloadPatientPdf(Long patientId) {
        FileEntity fileEntity = fileRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("No uploaded file found for patient with ID: " + patientId));
        return diskStorageService.load(fileEntity.getLocation());
    }

    @Transactional(readOnly = true)
    public String getPatientPdfName(Long patientId) {
        FileEntity fileEntity = fileRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("No uploaded file found for patient with ID: " + patientId));
        return fileEntity.getName();
    }
}
