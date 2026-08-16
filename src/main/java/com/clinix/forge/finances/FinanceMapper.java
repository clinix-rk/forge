package com.clinix.forge.finances;

import com.clinix.forge.finances.dto.FinanceResponse;
import com.clinix.forge.payments.entity.PaymentEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface FinanceMapper {

    @Mapping(target = "receiptNo", source = "paymentEntity", qualifiedByName = "generateReceiptNo")
    @Mapping(target = "date", source = "treatment.date")
    @Mapping(target = "caseNo", source = "patient.caseNo")
    @Mapping(target = "patientName", source = "patient.name")
    @Mapping(target = "patientId", source = "patient.id")
    FinanceResponse toResponse(PaymentEntity paymentEntity);

    @Named("generateReceiptNo")
    default String generateReceiptNo(PaymentEntity entity) {
        String paddedSerial = String.format("%05d", entity.getSerial());
        return String.format("%s/%s%s",
                entity.getFinancialYear(),
                entity.getDoctorIdentityCharacter(),
                paddedSerial
        );
    }
}
