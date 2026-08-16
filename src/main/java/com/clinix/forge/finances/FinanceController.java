package com.clinix.forge.finances;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginationMetadata;
import com.clinix.forge.core.pdf.PdfResponseUtil;
import com.clinix.forge.finances.dto.FinanceResponse;
import com.clinix.forge.payments.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/finances")
@RequiredArgsConstructor
@Tag(name = "Finance Management", description = "Endpoints for managing finances")
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping
    @Operation(summary = "Get finance information", description = "Retrieves finance information")
    public ResponseEntity<ApiResponse<List<FinanceResponse>>> getFinanceInfo(
            @RequestParam
            LocalDate startDate,

            @RequestParam
            LocalDate endDate,

            @RequestParam
            Long doctorId,

            @RequestParam
            String paymentMethod,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be smaller than 0")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 5, message = "Page number size can not be smaller than 5")
            @Max(value = 1000, message = "Page size must be less than or equal to 1000.")
            int pageSize
    ) {
        log.debug("Serving add payment request");
        Page<FinanceResponse> response = financeService.getFinanceData(
                startDate,
                endDate,
                doctorId,
                (PaymentMethod.valueOf(paymentMethod.toUpperCase())),
                pageNo,
                pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        response.getContent(),
                        new PaginationMetadata(response.getNumber(),
                                response.getSize(),
                                response.getTotalElements(),
                                response.getTotalPages(),
                                response.hasNext(),
                                response.hasPrevious())));
    }

    @GetMapping("/form")
    @Operation(summary = "Generate form 25", description = "Generates form 25 for the specified criteria")
    public ResponseEntity<byte[]> generateForm25(
            @RequestParam
            LocalDate startDate,

            @RequestParam
            LocalDate endDate,

            @RequestParam
            Long doctorId,

            @RequestParam
            String paymentMethod
    ) {
        log.debug("Api call : Generate form 25 pdf");
        byte[] pdf = financeService.generateForm25(
                startDate,
                endDate,
                doctorId,
                (PaymentMethod.valueOf(paymentMethod.toUpperCase())));

        return PdfResponseUtil.inline(pdf, "form-25.pdf");
    }

    @GetMapping("/form/summary")
    @Operation(summary = "Generate form 25 summary", description = "Generates form 25 summary for the specified criteria")
    public ResponseEntity<byte[]> generateForm25Summary(
            @RequestParam
            LocalDate startDate,

            @RequestParam
            LocalDate endDate,

            @RequestParam
            Long doctorId,

            @RequestParam
            String paymentMethod
    ) {
        log.debug("Api call : Generate form 25 summary pdf");
        byte[] pdf = financeService.generateForm25Summary(
                startDate,
                endDate,
                doctorId,
                (PaymentMethod.valueOf(paymentMethod.toUpperCase())));

        return PdfResponseUtil.inline(pdf, "form-25-summary.pdf");
    }
}
