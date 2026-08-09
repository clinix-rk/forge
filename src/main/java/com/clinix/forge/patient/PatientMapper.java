package com.clinix.forge.patient;

import com.clinix.forge.patient.dto.*;
import com.clinix.forge.patient.entity.DrugAllergyEntity;
import com.clinix.forge.patient.entity.MedicalConditionEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.entity.PhoneNumberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface PatientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "caseNo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "medicalConditions", ignore = true)
    @Mapping(target = "drugAllergies", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "serial", ignore = true)
    @Mapping(target = "phoneNumbers", ignore = true)
    PatientEntity toEntity(CreatePatientRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "caseNo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "medicalConditions", ignore = true)
    @Mapping(target = "drugAllergies", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "serial", ignore = true)
    @Mapping(target = "phoneNumbers", ignore = true)
    void updateEntityFromRequest(UpdatePatientRequest request, @MappingTarget PatientEntity entity);

    PatientResponse toResponse(PatientEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true)
    PhoneNumberEntity toPhoneEntity(PhoneNumberRequest request);

    PhoneNumberResponse toPhoneResponse(PhoneNumberEntity entity);

    // Custom helper methods for collections mapping
    default String mapMedicalCondition(MedicalConditionEntity condition) {
        return condition != null ? condition.getName() : null;
    }

    default String mapDrugAllergy(DrugAllergyEntity allergy) {
        return allergy != null ? allergy.getName() : null;
    }
}
