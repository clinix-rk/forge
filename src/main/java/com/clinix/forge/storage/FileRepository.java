package com.clinix.forge.storage;

import com.clinix.forge.storage.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing FileEntity objects.
 */
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByPatientId(Long patientId);

    Optional<FileEntity> findByName(String name);

    Optional<FileEntity> findByLocation(String location);
}
