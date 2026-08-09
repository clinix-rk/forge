package com.clinix.forge.catalog.prescription.instructions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing InstructionEntity objects.
 */
public interface InstructionRepository extends JpaRepository<InstructionEntity, Long> {
    Optional<InstructionEntity> findByInstruction(String instruction);
}
