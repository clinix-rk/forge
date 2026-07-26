package com.clinix.forge.catalog.medicines;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing MedicineEntity objects.
 */
public interface MedicineRepository extends JpaRepository<MedicineEntity, Long> {
    Optional<MedicineEntity> findByNameAndType(String name, String type);
}
