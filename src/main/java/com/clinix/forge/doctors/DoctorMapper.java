package com.clinix.forge.doctors;

import com.clinix.forge.doctors.dto.CreateDoctorRequest;
import com.clinix.forge.doctors.dto.DoctorResponse;
import com.clinix.forge.doctors.dto.UpdateDoctorRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface DoctorMapper {
    /**
     * Maps a {@link CreateDoctorRequest} to a {@link DoctorEntity} entity.
     *
     * @param dto the data transfer object containing creation details
     * @return the mapped {@link DoctorEntity} entity
     */
    DoctorEntity toEntity(CreateDoctorRequest dto);

    /**
     * Maps a {@link DoctorEntity} entity to a {@link DoctorResponse}.
     *
     * @param entity the doctor entity to map
     * @return the mapped {@link DoctorResponse}
     */
    DoctorResponse toDTO(DoctorEntity entity);

    /**
     * Updates an existing {@link DoctorEntity} entity with values from an {@link UpdateDoctorRequest}.
     * The ID and license number are ignored during this update to prevent accidental modification.
     *
     * @param dto the data transfer object containing update details
     * @param entity the existing doctor entity to update
     */
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateDoctorRequest dto, @MappingTarget DoctorEntity entity);
}
