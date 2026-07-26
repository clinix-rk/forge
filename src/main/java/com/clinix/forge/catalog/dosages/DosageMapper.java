package com.clinix.forge.catalog.dosages;

import com.clinix.forge.catalog.dosages.dto.CreateDrugDosageRequest;
import com.clinix.forge.catalog.dosages.dto.DrugDosageResponse;
import com.clinix.forge.catalog.dosages.dto.UpdateDrugDosageRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface DosageMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prescriptionMedicines", ignore = true)
    DosageEntity toDosageEntity(CreateDrugDosageRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prescriptionMedicines", ignore = true)
    void updateDosageFromRequest(UpdateDrugDosageRequest request, @MappingTarget DosageEntity entity);

    DrugDosageResponse toDosageResponse(DosageEntity entity);
}
