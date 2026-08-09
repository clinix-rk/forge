package com.clinix.forge.catalog.prescription.instructions;

import com.clinix.forge.catalog.prescription.instructions.dto.CreateInstructionRequest;
import com.clinix.forge.catalog.prescription.instructions.dto.InstructionResponse;
import com.clinix.forge.catalog.prescription.instructions.dto.UpdateInstructionRequest;
import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class InstructionService {

    private final InstructionRepository instructionRepository;
    private final InstructionMapper instructionMapper;

    @Transactional(rollbackFor = Exception.class)
    public InstructionResponse createDrugInstruction(CreateInstructionRequest request) {
        log.info("Creating drug instruction pattern: {}", request.instruction());

        if (instructionRepository.findByInstruction(request.instruction()).isPresent()) {
            throw new DuplicateResourceException("Drug instruction pattern '" + request.instruction() + "' already exists");
        }

        InstructionEntity entity = instructionMapper.toInstructionEntity(request);
        InstructionEntity saved = instructionRepository.save(entity);
        log.info("Drug instruction created with ID: {}", saved.getId());
        return instructionMapper.toInstructionResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<InstructionResponse> getAllDrugInstructions(int pageNo, int pageSize) {
        log.debug("Fetching drug instructions - PageNo: {}, PageSize: {}", pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<InstructionEntity> page = instructionRepository.findAll(pageRequest);
        return page.map(instructionMapper::toInstructionResponse);
    }

    @Transactional(readOnly = true)
    public InstructionResponse getDrugInstructionById(Long id) {
        log.debug("Fetching drug instruction with ID: {}", id);
        InstructionEntity entity = instructionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drug instruction not found with ID: " + id));
        return instructionMapper.toInstructionResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public InstructionResponse updateDrugInstructionById(Long id, UpdateInstructionRequest request) {
        log.info("Updating drug instruction with ID: {}", id);
        InstructionEntity entity = instructionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drug instruction not found with ID: " + id));

        if (!entity.getInstruction().equals(request.instruction()) && instructionRepository.findByInstruction(request.instruction()).isPresent()) {
            throw new DuplicateResourceException("Drug instruction pattern '" + request.instruction() + "' already exists");
        }

        instructionMapper.updateInstructionFromRequest(request, entity);
        InstructionEntity updated = instructionRepository.save(entity);
        log.info("Drug instruction updated with ID: {}", updated.getId());
        return instructionMapper.toInstructionResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDrugInstructionById(Long id) {
        log.info("Deleting drug instruction with ID: {}", id);
        if (!instructionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Drug instruction not found with ID: " + id);
        }
        instructionRepository.deleteById(id);
        log.info("Drug instruction deleted: {}", id);
    }
}
