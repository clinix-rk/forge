package com.clinix.forge.finance;

import com.clinix.forge.finance.dto.CreateReciptRequest;
import com.clinix.forge.finance.dto.ReciptResponse;
import com.clinix.forge.finance.dto.UpdateReciptRequest;
import com.clinix.forge.finance.entity.ReciptEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface ReciptMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "payments", ignore = true)
    ReciptEntity toEntity(CreateReciptRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "payments", ignore = true)
    void updateEntityFromRequest(UpdateReciptRequest request, @MappingTarget ReciptEntity entity);

    ReciptResponse toResponse(ReciptEntity entity);
}
