package com.clinix.forge.treatment.mapper;

import com.clinix.forge.treatment.dto.CreateTreatmentCategoryRequest;
import com.clinix.forge.treatment.dto.TreatmentCategoryResponse;
import com.clinix.forge.treatment.dto.UpdateTreatmentCategoryRequest;
import com.clinix.forge.treatment.entity.TreatmentCategoryEntity;
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
