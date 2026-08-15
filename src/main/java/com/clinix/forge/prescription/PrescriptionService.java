package com.clinix.forge.prescription;


import com.clinix.forge.catalog.medicines.MedicineEntity;
import com.clinix.forge.catalog.medicines.MedicineRepository;
import com.clinix.forge.catalog.prescription.dosages.DosageEntity;
import com.clinix.forge.catalog.prescription.dosages.DosageRepository;
import com.clinix.forge.catalog.prescription.instructions.InstructionEntity;
import com.clinix.forge.catalog.prescription.instructions.InstructionRepository;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.pdf.PdfGenerationService;
import com.clinix.forge.core.pdf.dto.PrescriptionMedicineItem;
import com.clinix.forge.core.pdf.dto.PrescriptionPdfData;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.prescription.dto.CreatePrescriptionRequest;
import com.clinix.forge.prescription.dto.PrescriptionMedicineRequest;
import com.clinix.forge.prescription.dto.PrescriptionResponse;
import com.clinix.forge.prescription.dto.UpdatePrescriptionRequest;
import com.clinix.forge.prescription.entity.PrescriptionEntity;
import com.clinix.forge.prescription.entity.PrescriptionMedicineEntity;
import com.clinix.forge.prescription.repositories.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final MedicineRepository medicineRepository;
    private final DosageRepository dosageRepository;
    private final InstructionRepository instructionRepository;

    private final PdfGenerationService pdfGenerationService;
    private final PrescriptionMapper prescriptionMapper;

    /**
     * Builds a set of {@link PrescriptionMedicineEntity} objects from the given
     * medicine request items, resolving each medicine and dosage from their
     * respective catalog repositories.
     */
    private Set<PrescriptionMedicineEntity> buildPrescriptionMedicines(
            List<PrescriptionMedicineRequest> medicines,
            PrescriptionEntity prescription) {

        Set<PrescriptionMedicineEntity> result = new HashSet<>();
        for (PrescriptionMedicineRequest item : medicines) {
            MedicineEntity medicine = medicineRepository.findById(item.medicineId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Medicine not found with ID: " + item.medicineId()));

            DosageEntity dosage = dosageRepository.findById(item.dosageId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Drug dosage not found with ID: " + item.dosageId()));

            InstructionEntity instruction = instructionRepository.findById(item.instructionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Drug dosage not found with ID: " + item.instructionId()));

            PrescriptionMedicineEntity entity = prescriptionMapper.toPrescriptionMedicineEntity(item);
            entity.setPrescription(prescription);
            entity.setMedicine(medicine);
            entity.setDosage(dosage);
            entity.setInstruction(instruction);
            result.add(entity);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public PrescriptionResponse createPrescription(Long patientId, CreatePrescriptionRequest request) {
        log.info("Creating prescription for patient ID: {}", patientId);

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        PrescriptionEntity prescription = prescriptionMapper.toPrescriptionEntity(request);
        prescription.setPatient(patient);

        if (request.medicines() != null && !request.medicines().isEmpty()) {
            Set<PrescriptionMedicineEntity> meds = buildPrescriptionMedicines(request.medicines(), prescription);
            prescription.setPrescriptionMedicines(meds);
        }

        PrescriptionEntity saved = prescriptionRepository.save(prescription);
        log.info("Prescription created with ID: {}", saved.getId());
        return prescriptionMapper.toPrescriptionResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> getAllPrescriptions(Long patientId, int pageNo, int pageSize) {
        log.debug("Fetching prescriptions - PatientId: {}, PageNo: {}, PageSize: {}", patientId, pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        if (patientId == null) {
            throw new ResourceNotFoundException("Patient with id " + patientId + " not found.");
        }

        Page<PrescriptionEntity> page = prescriptionRepository.findByPatientId(patientId, pageRequest);

        return page.map(prescriptionMapper::toPrescriptionResponse);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long patientId, Long id) {
        log.debug("Fetching prescription with ID: {}", id);
        PrescriptionEntity prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));
        return prescriptionMapper.toPrescriptionResponse(prescription);
    }

    @Transactional(rollbackFor = Exception.class)
    public PrescriptionResponse updatePrescriptionById(Long id, UpdatePrescriptionRequest request) {
        log.info("Updating prescription with ID: {}", id);
        PrescriptionEntity prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));

        prescriptionMapper.updatePrescriptionFromRequest(request, prescription);

        // Clear and rebuild prescriptionMedicines to avoid orphan removal and updates issues in JPA
        prescription.getPrescriptionMedicines().clear();
        if (request.medicines() != null && !request.medicines().isEmpty()) {
            Set<PrescriptionMedicineEntity> meds = buildPrescriptionMedicines(request.medicines(), prescription);
            prescription.getPrescriptionMedicines().addAll(meds);
        }

        PrescriptionEntity updated = prescriptionRepository.save(prescription);
        log.info("Prescription updated with ID: {}", updated.getId());
        return prescriptionMapper.toPrescriptionResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePrescriptionById(Long id) {
        log.info("Deleting prescription with ID: {}", id);
        if (!prescriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prescription not found with ID: " + id);
        }
        prescriptionRepository.deleteById(id);
        log.info("Prescription deleted: {}", id);
    }


    @Transactional(readOnly = true)
    public byte[] generatePrescriptionPdf(Long id) {
        log.info("Generating PDF for Prescription ID: {}", id);
        PrescriptionEntity prescription = prescriptionRepository.findByIdWithMedicines(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));

        PatientEntity patient = prescription.getPatient();
        String patientName = patient != null ? patient.getName() : "Unknown";
        String caseNo = patient.getCaseNo() != null ? patient.getCaseNo() : "Missing Case Number";

        String ageGender = "";
        if (patient != null) {
            String age = patient.getDateOfBirth() != null
                    ? java.time.Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears() + " yrs"
                    : "—";
            String gender = patient.getGender() != null ? patient.getGender().name() : "Unknown";
            ageGender = age + " / " + gender;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateStr = prescription.getCreatedAt() != null
                ? LocalDateTime.ofInstant(prescription.getCreatedAt(), ZoneId.systemDefault()).format(formatter)
                : LocalDate.now().format(formatter);

        List<PrescriptionMedicineItem> medicines = prescription.getPrescriptionMedicines().stream()
                .map(pm -> {
                    String medicineName = pm.getMedicine() != null ? pm.getMedicine().getName() : "Unknown";
                    String dosage = pm.getDosage() != null ? pm.getDosage().getDosage() : "—";
                    String instruction = pm.getInstruction() != null ? pm.getInstruction().getInstruction() : "";
                    return new PrescriptionMedicineItem(medicineName, dosage, instruction, pm.getQuantity());
                })
                .toList();

        PrescriptionPdfData data = new PrescriptionPdfData(
                caseNo,
                patientName.toUpperCase(),
                dateStr,
                ageGender,
                prescription.getDetails() != null ? prescription.getDetails() : "",
                medicines
        );

        Context context = new Context();
        context.setVariable("rx", data);

        try {
            return pdfGenerationService.generatePdf("pdf/prescription", context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
