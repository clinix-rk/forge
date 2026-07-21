package com.clinix.forge.prescription;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.prescription.dto.CreateMedicineRequest;
import com.clinix.forge.prescription.dto.MedicineResponse;
import com.clinix.forge.prescription.dto.UpdateMedicineRequest;
import com.clinix.forge.prescription.entity.MedicineEntity;
import com.clinix.forge.prescription.repositories.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final PrescriptionMapper prescriptionMapper;

    @Transactional(rollbackFor = Exception.class)
    public MedicineResponse createMedicine(CreateMedicineRequest request) {
        log.info("Creating medicine item: {} of type {}", request.name(), request.type());

        if (medicineRepository.findByNameAndType(request.name(), request.type()).isPresent()) {
            throw new DuplicateResourceException("Medicine '" + request.name() + "' of type '" + request.type() + "' already exists");
        }

        MedicineEntity entity = prescriptionMapper.toMedicineEntity(request);
        MedicineEntity saved = medicineRepository.save(entity);
        log.info("Medicine created with ID: {}", saved.getId());
        return prescriptionMapper.toMedicineResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<MedicineResponse> getAllMedicines(int pageNo, int pageSize) {
        log.debug("Fetching medicines - PageNo: {}, PageSize: {}", pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<MedicineEntity> page = medicineRepository.findAll(pageRequest);

        List<MedicineResponse> responses = page.getContent().stream()
                .map(prescriptionMapper::toMedicineResponse)
                .toList();

        return PaginatedPayload.of(responses, page);
    }

    @Transactional(readOnly = true)
    public MedicineResponse getMedicineById(Long id) {
        log.debug("Fetching medicine with ID: {}", id);
        MedicineEntity entity = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with ID: " + id));
        return prescriptionMapper.toMedicineResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public MedicineResponse updateMedicineById(Long id, UpdateMedicineRequest request) {
        log.info("Updating medicine with ID: {}", id);
        MedicineEntity entity = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with ID: " + id));

        if (!(entity.getName().equals(request.name()) && entity.getType().equals(request.type())) &&
                medicineRepository.findByNameAndType(request.name(), request.type()).isPresent()) {
            throw new DuplicateResourceException("Medicine '" + request.name() + "' of type '" + request.type() + "' already exists");
        }

        prescriptionMapper.updateMedicineFromRequest(request, entity);
        MedicineEntity updated = medicineRepository.save(entity);
        log.info("Medicine updated with ID: {}", updated.getId());
        return prescriptionMapper.toMedicineResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMedicineById(Long id) {
        log.info("Deleting medicine with ID: {}", id);
        if (!medicineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medicine not found with ID: " + id);
        }
        medicineRepository.deleteById(id);
        log.info("Medicine deleted: {}", id);
    }
}
