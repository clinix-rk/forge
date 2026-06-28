package com.clinix.forge.storage;

import com.clinix.forge.storage.dto.CreateFileRequest;
import com.clinix.forge.storage.dto.FileResponse;
import com.clinix.forge.storage.dto.UpdateFileRequest;
import com.clinix.forge.storage.entity.FileEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface FileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true) // Set manually in service
    FileEntity toEntity(CreateFileRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patient", ignore = true) // Do not modify patient relation
    void updateEntityFromRequest(UpdateFileRequest request, @MappingTarget FileEntity entity);

    @Mapping(target = "patientId", source = "patient.id")
    FileResponse toResponse(FileEntity entity);
}
