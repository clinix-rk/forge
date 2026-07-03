package com.clinix.forge.finance;

import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.pdf.PdfGenerationService;
import com.clinix.forge.core.pdf.dto.Form3CData;
import com.clinix.forge.core.pdf.dto.Form3CDateGroup;
import com.clinix.forge.core.pdf.dto.Form3CEntry;
import com.clinix.forge.doctors.DoctorEntity;
import com.clinix.forge.doctors.DoctorRepository;
import com.clinix.forge.finance.entity.PaymentEntity;
import com.clinix.forge.treatment.repository.TreatmentRepository;
import com.clinix.forge.treatment.entity.TreatmentEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Form3CPdfService {

    private final TreatmentRepository treatmentRepository;
    private final PaymentRepository paymentRepository;
    private final DoctorRepository doctorRepository;
    private final PdfGenerationService pdfGenerationService;

    @Transactional(readOnly = true)
    public byte[] generateForm3CPdf(LocalDate from, LocalDate to, Long doctorId) {
        log.info("Generating Form 3C PDF from {} to {} for doctorId: {}", from, to, doctorId);

        String doctorName = null;
        if (doctorId != null) {
            DoctorEntity doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
            doctorName = doctor.getName();
        }

        List<TreatmentEntity> treatments = treatmentRepository.findAllForForm3C(from, to, doctorId);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Map<LocalDate, List<Form3CEntry>> grouped = new LinkedHashMap<>();

        for (TreatmentEntity t : treatments) {
            Optional<PaymentEntity> paymentOpt = paymentRepository.findByTreatmentId(t.getId());
            double amount = paymentOpt.map(PaymentEntity::getAmount).orElse(0.0);

            String treatmentGiven = t.getCategory().getName() +
                    (t.getDetails() != null && !t.getDetails().trim().isEmpty() ? " — " + t.getDetails() : "");

            Form3CEntry entry = new Form3CEntry(
                    t.getId().intValue(),
                    t.getPatient().getCaseNo(),
                    t.getPatient().getName(),
                    treatmentGiven,
                    amount
            );

            grouped.computeIfAbsent(t.getDate(), k -> new ArrayList<>()).add(entry);
        }

        List<Form3CDateGroup> groups = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Form3CEntry>> entry : grouped.entrySet()) {
            String dateLabel = entry.getKey().format(dateFormatter);
            double dailyTotal = entry.getValue().stream().mapToDouble(Form3CEntry::amount).sum();
            groups.add(new Form3CDateGroup(dateLabel, entry.getValue(), dailyTotal));
        }

        double grandTotal = groups.stream().mapToDouble(Form3CDateGroup::dailyTotal).sum();

        Form3CData form3cData = new Form3CData(
                from.format(dateFormatter),
                to.format(dateFormatter),
                doctorName,
                1, // Page number placeholder or default
                groups,
                grandTotal
        );

        Context context = new Context();
        context.setVariable("form3c", form3cData);

        return pdfGenerationService.generatePdf("pdf/form3c", context);
    }
}
