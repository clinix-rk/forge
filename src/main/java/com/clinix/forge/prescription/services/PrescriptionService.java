package com.clinix.forge.prescription.services;

import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.prescription.PrescriptionMapper;
import com.clinix.forge.prescription.dto.*;
import com.clinix.forge.prescription.entity.DrugDosageEntity;
import com.clinix.forge.prescription.entity.MedicineEntity;
import com.clinix.forge.prescription.entity.PrescriptionEntity;
import com.clinix.forge.prescription.entity.PrescriptionMedicineEntity;
import com.clinix.forge.prescription.repositories.DrugDosageRepository;
import com.clinix.forge.prescription.repositories.MedicineRepository;
import com.clinix.forge.prescription.repositories.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.clinix.forge.core.pdf.PdfGenerationService;
import com.clinix.forge.core.pdf.dto.PrescriptionMedicineItem;
import com.clinix.forge.core.pdf.dto.PrescriptionPdfData;
import org.thymeleaf.context.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final MedicineRepository medicineRepository;
    private final DrugDosageRepository drugDosageRepository;
    private final PdfGenerationService pdfGenerationService;
    private final PrescriptionMapper prescriptionMapper;

    @Transactional(rollbackFor = Exception.class)
    public PrescriptionResponse createPrescription(CreatePrescriptionRequest request) {
        log.info("Creating prescription for patient ID: {}", request.patientId());

        PatientEntity patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.patientId()));

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
    public PaginatedPayload<PrescriptionResponse> getAllPrescriptions(int pageNo, int pageSize) {
        return getAllPrescriptions(null, pageNo, pageSize);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<PrescriptionResponse> getAllPrescriptions(Long patientId, int pageNo, int pageSize) {
        log.debug("Fetching prescriptions - PatientId: {}, PageNo: {}, PageSize: {}", patientId, pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<PrescriptionEntity> page = (patientId != null)
                ? prescriptionRepository.findByPatientId(patientId, pageRequest)
                : prescriptionRepository.findAll(pageRequest);

        List<PrescriptionResponse> responses = page.getContent().stream()
                .map(prescriptionMapper::toPrescriptionResponse)
                .toList();

        return PaginatedPayload.of(responses, page);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id) {
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

    private Set<PrescriptionMedicineEntity> buildPrescriptionMedicines(List<PrescriptionMedicineRequest> requests, PrescriptionEntity prescription) {
        Set<PrescriptionMedicineEntity> meds = new HashSet<>();
        for (PrescriptionMedicineRequest medRequest : requests) {
            MedicineEntity medicine = medicineRepository.findById(medRequest.medicineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with ID: " + medRequest.medicineId()));

            DrugDosageEntity dosage = drugDosageRepository.findById(medRequest.dosageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Drug dosage not found with ID: " + medRequest.dosageId()));

            PrescriptionMedicineEntity medEntity = prescriptionMapper.toPrescriptionMedicineEntity(medRequest);
            medEntity.setPrescription(prescription);
            medEntity.setMedicine(medicine);
            medEntity.setDosage(dosage);
            meds.add(medEntity);
        }
        return meds;
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
                    return new PrescriptionMedicineItem(medicineName, dosage, pm.getQuantity());
                })
                .toList();

        PrescriptionPdfData data = new PrescriptionPdfData(
                caseNo,
                patientName,
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
