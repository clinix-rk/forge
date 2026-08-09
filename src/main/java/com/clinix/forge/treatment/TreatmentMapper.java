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
public abstract class TreatmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "payment", ignore = true)
    public abstract TreatmentEntity toEntity(CreateTreatmentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "payment", ignore = true)
    public abstract void updateEntityFromRequest(UpdateTreatmentRequest request, @MappingTarget TreatmentEntity entity);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "patientId", source = "patient.id")
    public abstract TreatmentResponse mapTreatmentResponse(TreatmentEntity entity);

    public TreatmentResponse toResponse(TreatmentEntity entity, String categoryDisplay) {
        TreatmentResponse response = mapTreatmentResponse(entity);

        return new TreatmentResponse(
                response.id(),
                response.details(),
                response.date(),
                response.categoryId(),
                categoryDisplay,
                response.patientId(),
                response.createdAt(),
                response.updatedAt()
        );
    }
}
