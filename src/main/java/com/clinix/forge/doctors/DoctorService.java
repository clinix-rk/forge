package com.clinix.forge.doctors;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.doctors.dto.CreateDoctorRequest;
import com.clinix.forge.doctors.dto.DoctorResponse;
import com.clinix.forge.doctors.dto.UpdateDoctorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {

    /**
     * Service layer responsible for doctor-related business logic and
     * interactions with the persistence layer. All public methods are
     * transactional where appropriate and use the injected
     * {@link DoctorRepository} and {@link DoctorMapper} to perform CRUD
     * operations and DTO/entity mappings.
     */

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    /**
     * Create a new doctor record in the system.
     *
     * <p>This method validates the provided {@link CreateDoctorRequest}, checks
     * for business rule violations (duplicate case number prefix), maps the
     * DTO to an entity and persists it. The returned {@link Doctor}
     * represents the newly created resource.</p>
     *
     * @param dto DTO containing data required to create a doctor. Must not
     *            be null and will be validated via Jakarta Validation
     *            annotations.
     * @return the persisted doctor as a {@link Doctor}
     * @throws DuplicateResourceException if a doctor with the same case
     *                                    number prefix already exists
     */
    @Transactional(rollbackFor = Exception.class)
    public DoctorResponse createDoctor(CreateDoctorRequest dto) {
        log.debug("Registering new doctor : { name: {}, caseNoPrefix: {} }", dto.name(), dto.caseNoPrefix());

        if(doctorRepository.existsByCaseNoPrefix(dto.caseNoPrefix())) {
            throw new DuplicateResourceException("Doctor with the same case number prefix already exists.");
        }

        DoctorEntity doctor = doctorMapper.toEntity(dto);
        DoctorEntity savedDoctor = doctorRepository.save(doctor);

        return doctorMapper.toDTO(savedDoctor);
    }

    /**
     * Retrieve a paginated list of doctors.
     *
     * <p>This method performs a read-only transactional query to the
     * repository and maps the resulting {@code Page<Doctor>} to
     * {@code Page<DoctorDTO>} using {@link DoctorMapper}.</p>
     *
     * @param pageable pagination and sorting information for the query.
     *                 The method will enforce a maximum page size to prevent excessive data retrieval.
     * @return a {@code Page<DoctorDTO>} representing the requested page
     */
    @Transactional(readOnly = true)
    public PaginatedPayload<DoctorResponse> getPaginatedDoctors(Pageable pageable) {
        log.debug("Fetching doctor information with pagination constraints: {}", pageable);

        int maxSize = 1000;
        Pageable securePageable = pageable;
        if (pageable.getPageSize() > maxSize) {
            log.warn("Requested page size {} exceeds maximum limit. Capping to {}.", pageable.getPageSize(), maxSize);
            securePageable = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        Page<DoctorEntity> doctorPage = doctorRepository.findAll(securePageable);

        List<DoctorResponse> dtoList = doctorPage.getContent().stream()
                .map(doctorMapper::toDTO)
                .toList();

        return PaginatedPayload.of(dtoList, doctorPage);
    }

    /**
     * Fetch a single doctor by its identifier.
     *
     * @param id the database identifier of the doctor to fetch
     * @return a {@link Doctor} representing the requested doctor
     * @throws ResourceNotFoundException if no doctor exists with the given id
     */
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        log.debug("Fetching doctor information for doctor with ID: {}", id);
        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() -> {
            log.warn("Doctor fetch failed: ID {} not found", id);
            return new ResourceNotFoundException("Doctor not found with ID: " + id);
        });

        log.debug("Doctor with ID {} fetched successfully.", id);
        return doctorMapper.toDTO(doctor);
    }

    /**
     * Update an existing doctor's information.
     *
     * <p>This method locates the doctor by id, applies changes from the
     * provided {@link UpdateDoctorRequest} onto the entity using the
     * {@link DoctorMapper} and persists the updated entity.</p>
     *
     * @param id  identifier of the doctor to update
     * @param dto DTO containing fields to update (partial updates are
     *            supported depending on mapper behavior)
     * @return the updated {@link Doctor}
     * @throws ResourceNotFoundException if no doctor exists with the given id
     */
    @Transactional(rollbackFor = Exception.class)
    public DoctorResponse updateDoctorById(Long id, UpdateDoctorRequest dto) {
        log.debug("Attempting to update doctor with ID: {}. Update data: {}", id, dto);

        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() -> {
            log.warn("Doctor update failed. Id {} not found", id);
            return new ResourceNotFoundException("Doctor not found with ID: " + id);
        });

        doctorMapper.updateEntityFromDto(dto, doctor);

        DoctorEntity updatedDoctor = doctorRepository.save(doctor);

        log.debug("Doctor with ID {} updated successfully.", id);
        return doctorMapper.toDTO(updatedDoctor);
    }

    /**
     * Delete a doctor by id.
     *
     * @param id identifier of the doctor to delete
     * @throws ResourceNotFoundException if no doctor exists with the given id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDoctorById(Long id) {
        log.debug("Attempting to delete doctor with ID: {}.", id);

        if(!doctorRepository.existsById(id)) {
            log.warn("Doctor deletion failed. ID {} not found.", id);
            throw new ResourceNotFoundException("Doctor not found with ID: " + id);
        }

        doctorRepository.deleteById(id);

        log.debug("Doctor with ID {} deleted successfully.", id);
    }

    /**
     * Search for doctors by name fragment.
     *
     * @param name the name fragment to search for
     * @return a list of matching doctors mapped to DoctorResponse DTOs
     */
    @Transactional(readOnly = true)
    public List<DoctorResponse> searchDoctorsByName(String name) {
        log.debug("Searching doctors by name matching: {}", name);
        return doctorRepository.findByNameContainingIgnoreCase(name).stream()
                .map(doctorMapper::toDTO)
                .toList();
    }
}

