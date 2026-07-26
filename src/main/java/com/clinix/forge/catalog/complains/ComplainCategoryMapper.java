package com.clinix.forge.catalog.complains;

import com.clinix.forge.catalog.complains.dto.ComplainCategoryResponse;
import com.clinix.forge.catalog.complains.dto.CreateComplainCategoryRequest;
import com.clinix.forge.catalog.complains.dto.UpdateComplainCategoryRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface ComplainCategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "complains", ignore = true)
    ComplainCategoryEntity toComplainCategoryEntity(CreateComplainCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "complains", ignore = true)
    void updateComplainCategoryFromRequest(UpdateComplainCategoryRequest request, @MappingTarget ComplainCategoryEntity entity);

    @Mapping(target = "parentId", source = "parent.id")
    ComplainCategoryResponse toComplainCategoryResponse(ComplainCategoryEntity entity);
}
