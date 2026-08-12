package com.clinix.forge.patient;

import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.doctors.DoctorEntity;
import com.clinix.forge.doctors.DoctorRepository;
import com.clinix.forge.patient.dto.CreatePatientRequest;
import com.clinix.forge.patient.dto.PatientResponse;
import com.clinix.forge.patient.dto.PhoneNumberRequest;
import com.clinix.forge.patient.dto.UpdatePatientRequest;
import com.clinix.forge.patient.entity.*;
import com.clinix.forge.patient.repositories.DrugAllergyRepository;
import com.clinix.forge.patient.repositories.MedicalConditionRepository;
import com.clinix.forge.patient.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalConditionRepository medicalConditionRepository;
    private final DrugAllergyRepository drugAllergyRepository;
    private final PatientMapper patientMapper;

    /**
     * Create patient record in DB for the given patient data.
     * Generates a case number based on the doctor's prefix and increments the doctor's count.
     *
     * @param request Patient data to be recorded
     * @return Patient response
     */
    @Transactional(rollbackFor = Exception.class)
    public PatientResponse createPatient(CreatePatientRequest request) {
        log.info("Creating patient with name: {} under doctor ID: {}", request.name(), request.doctorId());

        DoctorEntity doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> {
                    log.warn("Doctor with ID {} not found during patient creation", request.doctorId());
                    return new ResourceNotFoundException("Doctor with id " + request.doctorId() + " not found");
                });

        long totalCasesForDoctor = patientRepository.findMaxSerialByDoctorId(request.doctorId()).orElse(0);
        Long newSerial = totalCasesForDoctor + 1;

        String generatedCaseNo = PatientUtility.generateCaseNo(doctor.getCaseNoPrefix(), newSerial);
        log.debug("Generated case number: {} for new patient serial: {}", generatedCaseNo, newSerial);

        PatientEntity patientEntity = patientMapper.toEntity(request);
        patientEntity.setCaseNo(generatedCaseNo);
        patientEntity.setSerial(newSerial);
        patientEntity.setDoctor(doctor);

        // Resolve medical conditions and drug allergies (look up existing or create new)
        patientEntity.setMedicalConditions(resolveMedicalConditions(request.medicalConditions()));
        patientEntity.setDrugAllergies(resolveDrugAllergies(request.drugAllergies()));

        // Resolve phone numbers
        if (request.phoneNumbers() != null) {
            Set<PhoneNumberEntity> phoneEntities = request.phoneNumbers().stream()
                    .map(phoneReq -> {
                        PhoneNumberEntity phoneEntity = patientMapper.toPhoneEntity(phoneReq);
                        phoneEntity.setPatient(patientEntity);
                        return phoneEntity;
                    })
                    .collect(Collectors.toSet());
            patientEntity.setPhoneNumbers(phoneEntities);
        }

        PatientEntity savedPatient = patientRepository.save(patientEntity);
        log.info("Patient created successfully with ID: {} and Case No: {}", savedPatient.getId(), savedPatient.getCaseNo());
        return patientMapper.toResponse(savedPatient);
    }

    /**
     * Get a paginated and filtered list of patients based on search parameters.
     *
     * @param name     Filter name
     * @param phoneNo  Filter phone number
     * @param caseNo   Filter case number
     * @param pageNo   1-based page number
     * @param pageSize page size limit
     * @return Paginated list of patient responses
     */
    @Transactional(readOnly = true)
    public Page<PatientResponse> searchPatients(
            String name, String phoneNo, String caseNo, int pageNo, int pageSize) {
        log.debug("Searching patients - Name: {}, Phone: {}, CaseNo: {}, PageNo: {}, PageSize: {}",
                name, phoneNo, caseNo, pageNo, pageSize);

        // Perform validations and sanitation using utility
        PatientUtility.validateSearchParameters(name, phoneNo, caseNo);
        String sanitizedName = PatientUtility.sanitizeSearchTerm(name);
        String sanitizedPhoneNo = PatientUtility.sanitizeSearchTerm(phoneNo);
        String sanitizedCaseNo = PatientUtility.sanitizeSearchTerm(caseNo);

        // Use 0-based page index directly
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<PatientEntity> patientPage = patientRepository.searchPatients(
                sanitizedName, sanitizedCaseNo, sanitizedPhoneNo, pageRequest);

        return patientPage.map(patientMapper::toResponse);
    }

    /**
     * Get patient by providing database ID.
     *
     * @param id Patient ID
     * @return Patient response
     */
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        log.debug("Fetching patient with ID: {}", id);
        PatientEntity patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        return patientMapper.toResponse(patient);
    }

    /**
     * Get patient by providing case number.
     *
     * @param caseNo Patient case number
     * @return Patient response
     */
    @Transactional(readOnly = true)
    public PatientResponse getPatientByCaseNo(String caseNo) {
        log.debug("Fetching patient with Case No: {}", caseNo);
        PatientEntity patient = patientRepository.findByCaseNo(caseNo)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with Case No: " + caseNo));
        return patientMapper.toResponse(patient);
    }

    /**
     * Update patient record by ID.
     *
     * @param id      The patient ID to update
     * @param request Update request data
     * @return Updated patient response
     */
    @Transactional(rollbackFor = Exception.class)
    public PatientResponse updatePatientById(Long id, UpdatePatientRequest request) {
        log.info("Updating patient with ID: {}", id);
        PatientEntity patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

        patientMapper.updateEntityFromRequest(request, patient);

        // Resolve and update medical conditions and drug allergies
        patient.setMedicalConditions(resolveMedicalConditions(request.medicalConditions()));
        patient.setDrugAllergies(resolveDrugAllergies(request.drugAllergies()));

        // In-place update of phone numbers to preserve Hibernate tracking and prevent unique constraint violations
        if (request.phoneNumbers() != null) {
            Map<PhoneType, PhoneNumberEntity> existingPhones = patient.getPhoneNumbers().stream()
                    .collect(Collectors.toMap(PhoneNumberEntity::getType, phone -> phone));

            Set<PhoneType> requestedTypes = request.phoneNumbers().stream()
                    .map(PhoneNumberRequest::type)
                    .collect(Collectors.toSet());

            // 1. Remove phone numbers whose types are no longer requested
            patient.getPhoneNumbers().removeIf(phone -> {
                boolean remove = !requestedTypes.contains(phone.getType());
                if (remove) {
                    phone.setPatient(null);
                }
                return remove;
            });

            // 2. Update existing phone numbers or add new ones
            for (var phoneReq : request.phoneNumbers()) {
                PhoneNumberEntity existingPhone = existingPhones.get(phoneReq.type());
                if (existingPhone != null) {
                    if (!existingPhone.getPhoneNumber().equals(phoneReq.phoneNumber())) {
                        existingPhone.setPhoneNumber(phoneReq.phoneNumber());
                    }
                } else {
                    PhoneNumberEntity newPhone = patientMapper.toPhoneEntity(phoneReq);
                    newPhone.setPatient(patient);
                    patient.getPhoneNumbers().add(newPhone);
                }
            }
        } else {
            for (var phone : patient.getPhoneNumbers()) {
                phone.setPatient(null);
            }
            patient.getPhoneNumbers().clear();
        }

        PatientEntity updatedPatient = patientRepository.save(patient);
        log.info("Patient updated successfully with ID: {}", updatedPatient.getId());
        return patientMapper.toResponse(updatedPatient);
    }

    /**
     * Delete patient record by ID.
     *
     * @param id The patient ID to delete
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePatientById(Long id) {
        log.info("Deleting patient with ID: {}", id);
        if (!patientRepository.existsById(id)) {
            log.warn("Patient not found for deletion with ID: {}", id);
            throw new ResourceNotFoundException("Patient not found with ID: " + id);
        }
        patientRepository.deleteById(id);
        log.info("Patient deleted successfully: {}", id);
    }

    @Transactional(readOnly = true)
    public List<String> getAllMedicalConditions() {
        return medicalConditionRepository.findAll().stream()
                .map(MedicalConditionEntity::getName)
                .sorted()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getAllDrugAllergies() {
        return drugAllergyRepository.findAll().stream()
                .map(DrugAllergyEntity::getName)
                .sorted()
                .toList();
    }

    private Set<MedicalConditionEntity> resolveMedicalConditions(Set<String> conditionNames) {
        if (conditionNames == null || conditionNames.isEmpty()) {
            return new HashSet<>();
        }
        Set<MedicalConditionEntity> resolved = new HashSet<>();
        for (String name : conditionNames) {
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            String trimmedName = name.trim();
            MedicalConditionEntity condition = medicalConditionRepository.findByNameIgnoreCase(trimmedName)
                    .orElseGet(() -> medicalConditionRepository.save(
                            MedicalConditionEntity.builder().name(trimmedName).build()
                    ));
            resolved.add(condition);
        }
        return resolved;
    }

    private Set<DrugAllergyEntity> resolveDrugAllergies(Set<String> allergyNames) {
        if (allergyNames == null || allergyNames.isEmpty()) {
            return new HashSet<>();
        }
        Set<DrugAllergyEntity> resolved = new HashSet<>();
        for (String name : allergyNames) {
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            String trimmedName = name.trim();
            DrugAllergyEntity allergy = drugAllergyRepository.findByNameIgnoreCase(trimmedName)
                    .orElseGet(() -> drugAllergyRepository.save(
                            DrugAllergyEntity.builder().name(trimmedName).build()
                    ));
            resolved.add(allergy);
        }
        return resolved;
    }
}
