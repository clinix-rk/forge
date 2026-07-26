package com.clinix.forge.complain.mapper;

import com.clinix.forge.complain.dto.ComplainCategoryResponse;
import com.clinix.forge.complain.dto.CreateComplainCategoryRequest;
import com.clinix.forge.complain.dto.UpdateComplainCategoryRequest;
import com.clinix.forge.complain.entity.ComplainCategoryEntity;
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
