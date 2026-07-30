package com.clinix.forge.treatment;

import com.clinix.forge.treatment.dto.CreateTreatmentRequest;
import com.clinix.forge.treatment.dto.TreatmentResponse;
import com.clinix.forge.treatment.dto.UpdateTreatmentRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface TreatmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)      // Set manually in service
    @Mapping(target = "patient", ignore = true)       // Set manually in service
    @Mapping(target = "payment", ignore = true)
    TreatmentEntity toEntity(CreateTreatmentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)      // Set manually in service if category changes
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "payment", ignore = true)
    void updateEntityFromRequest(UpdateTreatmentRequest request, @MappingTarget TreatmentEntity entity);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "patientId", source = "patient.id")
    TreatmentResponse toResponse(TreatmentEntity entity);
}
