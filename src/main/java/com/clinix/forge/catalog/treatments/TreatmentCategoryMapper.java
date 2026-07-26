package com.clinix.forge.catalog.treatments;

import com.clinix.forge.catalog.treatments.dto.CreateTreatmentCategoryRequest;
import com.clinix.forge.catalog.treatments.dto.TreatmentCategoryResponse;
import com.clinix.forge.catalog.treatments.dto.UpdateTreatmentCategoryRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface TreatmentCategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "treatments", ignore = true)
    TreatmentCategoryEntity toTreatmentCategoryEntity(CreateTreatmentCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "treatments", ignore = true)
    void updateTreatmentCategory(UpdateTreatmentCategoryRequest request, @MappingTarget TreatmentCategoryEntity entity);

    @Mapping(target = "parentId", source = "parent.id")
    TreatmentCategoryResponse toTreatmentCategoryResponse(TreatmentCategoryEntity entity);
}
