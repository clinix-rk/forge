package com.clinix.forge.doctors;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link DoctorEntity} entities.
 * Handles data access operations for doctors.
 */
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {

    /**
     * Checks if a doctor exists with the given case number prefix.
     *
     * @param caseNoPrefix the case number prefix to check for existence
     * @return true if a doctor with the specified prefix already exists, false otherwise
     */
    boolean existsByCaseNoPrefix(String caseNoPrefix);

    /**
     * Search for doctors by name fragment, case insensitively.
     *
     * @param name the name fragment to search for
     * @return list of matching doctor entities
     */
    java.util.List<DoctorEntity> findByNameContainingIgnoreCase(String name);
}

