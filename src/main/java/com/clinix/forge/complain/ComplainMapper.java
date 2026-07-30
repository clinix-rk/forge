package com.clinix.forge.complain;

import com.clinix.forge.complain.dto.ComplainResponse;
import com.clinix.forge.complain.dto.CreateComplainRequest;
import com.clinix.forge.complain.dto.UpdateComplainRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface ComplainMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "patient", ignore = true)
    ComplainEntity toEntity(CreateComplainRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateEntityFromRequest(UpdateComplainRequest request, @MappingTarget ComplainEntity entity);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "patientId", source = "patient.id")
    ComplainResponse toResponse(ComplainEntity entity);
}
