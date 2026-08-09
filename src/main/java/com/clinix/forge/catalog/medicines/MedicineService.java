package com.clinix.forge.catalog.medicines;

import com.clinix.forge.catalog.medicines.dto.CreateMedicineRequest;
import com.clinix.forge.catalog.medicines.dto.MedicineResponse;
import com.clinix.forge.catalog.medicines.dto.UpdateMedicineRequest;
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
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;

    @Transactional(rollbackFor = Exception.class)
    public MedicineResponse createMedicine(CreateMedicineRequest request) {
        log.info("Creating medicine item: {} of type {}", request.name(), request.type());

        if (medicineRepository.findByNameAndType(request.name(), request.type()).isPresent()) {
            throw new DuplicateResourceException("Medicine '" + request.name() + "' of type '" + request.type() + "' already exists");
        }

        MedicineEntity entity = medicineMapper.toMedicineEntity(request);
        MedicineEntity saved = medicineRepository.save(entity);
        log.info("Medicine created with ID: {}", saved.getId());
        return medicineMapper.toMedicineResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<MedicineResponse> getAllMedicines(int pageNo, int pageSize) {
        log.debug("Fetching medicines - PageNo: {}, PageSize: {}", pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<MedicineEntity> page = medicineRepository.findAll(pageRequest);
        return page.map(medicineMapper::toMedicineResponse);
    }

    @Transactional(readOnly = true)
    public MedicineResponse getMedicineById(Long id) {
        log.debug("Fetching medicine with ID: {}", id);
        MedicineEntity entity = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with ID: " + id));
        return medicineMapper.toMedicineResponse(entity);
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

        medicineMapper.updateMedicineFromRequest(request, entity);
        MedicineEntity updated = medicineRepository.save(entity);
        log.info("Medicine updated with ID: {}", updated.getId());
        return medicineMapper.toMedicineResponse(updated);
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
