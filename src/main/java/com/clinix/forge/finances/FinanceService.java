package com.clinix.forge.finances;

import com.clinix.forge.core.pdf.PdfGenerationService;
import com.clinix.forge.core.pdf.dto.Form25CaseEntry;
import com.clinix.forge.core.pdf.dto.Form25SummaryEntry;
import com.clinix.forge.finances.dto.FinanceResponse;
import com.clinix.forge.payments.PaymentRepository;
import com.clinix.forge.payments.entity.PaymentEntity;
import com.clinix.forge.payments.entity.PaymentMethod;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FinanceService {
    private final PaymentRepository paymentRepository;
    private final FinanceMapper financeMapper;
    private final PdfGenerationService pdfGenerationService;

    @Transactional(readOnly = true)
    public Page<FinanceResponse> getFinanceData(
            LocalDate startDate,
            LocalDate endDate,
            Long doctorId,
            PaymentMethod paymentMethod,
            int pageNo,
            int pageSize
    ) {
        log.debug("Gathering payment data from {} to {} for payment method {} and doctor id {}.",
                startDate, endDate, paymentMethod, doctorId);

        Specification<PaymentEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.between(root.get("treatment").get("date"), startDate, endDate));

            if (doctorId != null) {
                predicates.add(cb.equal(root.get("patient").get("doctor").get("id"), doctorId));
            }
            if (paymentMethod != PaymentMethod.ALL) {
                predicates.add(cb.equal(root.get("method"), paymentMethod));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Order.asc("treatment.date"));
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);

        Page<PaymentEntity> page = paymentRepository.findAll(spec, pageRequest);

        return page.map(financeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public byte[] generateForm25(
            LocalDate startDate,
            LocalDate endDate,
            Long doctorId,
            PaymentMethod paymentMethod
    ) {
        Specification<PaymentEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.between(root.get("treatment").get("date"), startDate, endDate));

            if (doctorId != null) {
                predicates.add(cb.equal(root.get("patient").get("doctor").get("id"), doctorId));
            }
            if (paymentMethod != PaymentMethod.ALL) {
                predicates.add(cb.equal(root.get("method"), paymentMethod));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Order.asc("treatment.date"));

        List<PaymentEntity> payments = paymentRepository.findAll(spec, sort);

        log.debug("Generating Form 25 for {} to {}", startDate, endDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Form25CaseEntry> caseEntries = payments.stream()
                .filter(payment -> payment.getAmount().longValue() != 0)
                .map(payment -> new Form25CaseEntry(
                        payment.getPatient().getCaseNo(),
                        payment.getTreatment().getDate().format(formatter),
                        payment.getPatient().getName(),
                        payment.getTreatmentDetails(),
                        payment.getAmount().toString(),
                        payment.getReceivedDate().format(formatter)
                ))
                .collect(Collectors.toList());

        Context context = new Context();
        context.setVariable("caseEntries", caseEntries);

        try {
            return pdfGenerationService.generatePdf("pdf/form-25", context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateForm25Summary(
            LocalDate startDate,
            LocalDate endDate,
            Long doctorId,
            PaymentMethod paymentMethod
    ) {
        Specification<PaymentEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.between(root.get("treatment").get("date"), startDate, endDate));

            if (doctorId != null) {
                predicates.add(cb.equal(root.get("patient").get("doctor").get("id"), doctorId));
            }
            if (paymentMethod != PaymentMethod.ALL) {
                predicates.add(cb.equal(root.get("method"), paymentMethod));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Order.asc("treatment.date"));

        List<PaymentEntity> payments = paymentRepository.findAll(spec, sort);

        log.debug("Generating Form 25 summary for {} to {}", startDate, endDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<LocalDate, double[]> byDate = new TreeMap<>();
        for (PaymentEntity payment : payments) {
            LocalDate date = payment.getTreatment().getDate();
            double amount = payment.getAmount();
            double[] agg = byDate.computeIfAbsent(date, d -> new double[3]);

            if (payment.getMethod() == PaymentMethod.CASH) {
                agg[0] += amount;
            } else if (payment.getMethod() == PaymentMethod.CHEQUE || payment.getMethod() == PaymentMethod.ONLINE) {
                agg[1] += amount;
            }
            agg[2] += amount;
        }

        double cashTotal = 0, digitalTotal = 0, overallTotal = 0;
        List<Form25SummaryEntry> summaryEntries = new ArrayList<>();
        for (Map.Entry<LocalDate, double[]> entry : byDate.entrySet()) {
            double[] agg = entry.getValue();
            cashTotal += agg[0];
            digitalTotal += agg[1];
            overallTotal += agg[2];
            summaryEntries.add(new Form25SummaryEntry(
                    entry.getKey().format(formatter),
                    Double.toString(agg[0]),
                    Double.toString(agg[1]),
                    Double.toString(agg[2])
            ));
        }

        Context context = new Context();
        context.setVariable("summaryEntries", summaryEntries);
        context.setVariable("startDate", startDate.format(formatter));
        context.setVariable("endDate", endDate.format(formatter));
        context.setVariable("grandTotalCash", Double.toString(cashTotal));
        context.setVariable("grandTotalDigital", Double.toString(digitalTotal));
        context.setVariable("grandTotalOverall", Double.toString(overallTotal));

        try {
            return pdfGenerationService.generatePdf("pdf/form-25-summary", context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
