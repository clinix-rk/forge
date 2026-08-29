package com.clinix.forge.doctors;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link DoctorEntity}.
 * Handles data access operations for doctors.
 */
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {

    /**
     * Checks if a doctor exists with the given case number prefix.
     *
     * @param caseNoPrefix the case number prefix to check for existence
     * @return true if a doctor with the specified prefix already exists false otherwise
     */
    boolean existsByCaseNoPrefix(String caseNoPrefix);

    /**
     * Case-insensitive search for doctors by name.
     *
     * @param name the name to search for
     * @return list of matching {@link DoctorEntity} objects.
     */
    java.util.List<DoctorEntity> findByNameContainingIgnoreCase(String name);
}
