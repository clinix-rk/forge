package com.clinix.forge.appointment;

import com.clinix.forge.appointment.dto.CreateAppointmentRequest;
import com.clinix.forge.appointment.dto.AppointmentResponse;
import com.clinix.forge.appointment.dto.UpdateAppointmentRequest;
import com.clinix.forge.appointment.entity.AppointmentEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true) // Set manually in service
    AppointmentEntity toEntity(CreateAppointmentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true) // Do not modify patient relation
    void updateEntityFromRequest(UpdateAppointmentRequest request, @MappingTarget AppointmentEntity entity);

    @Mapping(target = "patientId", source = "patient.id")
    AppointmentResponse toResponse(AppointmentEntity entity);
}
