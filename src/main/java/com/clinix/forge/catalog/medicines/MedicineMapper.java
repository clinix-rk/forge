package com.clinix.forge.catalog.medicines;

import com.clinix.forge.catalog.medicines.dto.CreateMedicineRequest;
import com.clinix.forge.catalog.medicines.dto.MedicineResponse;
import com.clinix.forge.catalog.medicines.dto.UpdateMedicineRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface MedicineMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prescriptionMedicines", ignore = true)
    MedicineEntity toMedicineEntity(CreateMedicineRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prescriptionMedicines", ignore = true)
    void updateMedicineFromRequest(UpdateMedicineRequest request, @MappingTarget MedicineEntity entity);

    MedicineResponse toMedicineResponse(MedicineEntity entity);
}
