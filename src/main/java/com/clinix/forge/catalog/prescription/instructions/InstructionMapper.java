package com.clinix.forge.catalog.prescription.instructions;

import com.clinix.forge.catalog.prescription.instructions.dto.CreateInstructionRequest;
import com.clinix.forge.catalog.prescription.instructions.dto.InstructionResponse;
import com.clinix.forge.catalog.prescription.instructions.dto.UpdateInstructionRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface InstructionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prescriptionMedicines", ignore = true)
    InstructionEntity toInstructionEntity(CreateInstructionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prescriptionMedicines", ignore = true)
    void updateInstructionFromRequest(UpdateInstructionRequest request, @MappingTarget InstructionEntity entity);

    InstructionResponse toInstructionResponse(InstructionEntity entity);
}
