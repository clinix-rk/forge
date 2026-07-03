package com.clinix.forge.finance;

import com.clinix.forge.finance.dto.CreatePaymentRequest;
import com.clinix.forge.finance.dto.EnrichedPaymentResponse;
import com.clinix.forge.finance.dto.PaymentResponse;
import com.clinix.forge.finance.dto.UpdatePaymentRequest;
import com.clinix.forge.finance.entity.PaymentEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "recipt", ignore = true)        // Set manually in service
    @Mapping(target = "treatment", ignore = true)     // Set manually in service
    PaymentEntity toEntity(CreatePaymentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "recipt", ignore = true)
    @Mapping(target = "treatment", ignore = true)
    void updateEntityFromRequest(UpdatePaymentRequest request, @MappingTarget PaymentEntity entity);

    @Mapping(target = "reciptId", source = "recipt.id")
    @Mapping(target = "treatmentId", source = "treatment.id")
    PaymentResponse toResponse(PaymentEntity entity);

    @Mapping(target = "reciptId", source = "recipt.id")
    @Mapping(target = "treatmentId", source = "treatment.id")
    @Mapping(target = "patientId", source = "treatment.patient.id")
    @Mapping(target = "patientName", source = "treatment.patient.name")
    @Mapping(target = "patientCaseNo", source = "treatment.patient.caseNo")
    EnrichedPaymentResponse toEnrichedResponse(PaymentEntity entity);
}
