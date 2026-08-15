package com.clinix.forge.prescription;

import com.clinix.forge.prescription.dto.*;
import com.clinix.forge.prescription.entity.PrescriptionEntity;
import com.clinix.forge.prescription.entity.PrescriptionMedicineEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface PrescriptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true)       // Set manually in service
    @Mapping(target = "prescriptionMedicines", ignore = true)
    PrescriptionEntity toPrescriptionEntity(CreatePrescriptionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "prescriptionMedicines", ignore = true)
    void updatePrescriptionFromRequest(UpdatePrescriptionRequest request, @MappingTarget PrescriptionEntity entity);

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "medicines", source = "prescriptionMedicines")
    PrescriptionResponse toPrescriptionResponse(PrescriptionEntity entity);

    // PrescriptionMedicine mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prescription", ignore = true)
    @Mapping(target = "medicine", ignore = true)
    @Mapping(target = "dosage", ignore = true)
    @Mapping(target = "instruction", ignore = true)
    PrescriptionMedicineEntity toPrescriptionMedicineEntity(PrescriptionMedicineRequest request);

    @Mapping(target = "medicineId", source = "medicine.id")
    @Mapping(target = "dosageId", source = "dosage.id")
    @Mapping(target = "instructionId", source = "instruction.id")
    PrescriptionMedicineResponse toPrescriptionMedicineResponse(PrescriptionMedicineEntity entity);
}
