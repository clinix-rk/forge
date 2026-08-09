package com.clinix.forge.suggestion;

import com.clinix.forge.suggestion.dto.CreateSuggestionRequest;
import com.clinix.forge.suggestion.dto.SuggestionResponse;
import com.clinix.forge.suggestion.dto.UpdateSuggestionRequest;
import com.clinix.forge.suggestion.entity.SuggestionEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface SuggestionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "category", ignore = true)
    SuggestionEntity toEntity(CreateSuggestionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromRequest(UpdateSuggestionRequest request, @MappingTarget SuggestionEntity entity);

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "categoryId", source = "category.id")
    SuggestionResponse toResponse(SuggestionEntity entity);
}
