package com.clinix.forge.payments;

import com.clinix.forge.payments.dto.CreatePaymentRequest;
import com.clinix.forge.payments.dto.PaymentResponse;
import com.clinix.forge.payments.dto.UpdatePaymentRequest;
import com.clinix.forge.payments.entity.PaymentEntity;
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
    @Mapping(target = "treatment", ignore = true)
    PaymentEntity toEntity(CreatePaymentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "treatment", ignore = true)
    void updateEntityFromRequest(UpdatePaymentRequest request, @MappingTarget PaymentEntity entity);

    @Mapping(target = "treatmentId", source = "treatment.id")
    @Mapping(target = "receiptNo", source = "entity", qualifiedByName = "generateReceiptNo")
    PaymentResponse toResponse(PaymentEntity entity);

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
