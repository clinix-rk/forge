package com.clinix.forge.user;

import com.clinix.forge.user.dto.CreateUserRequest;
import com.clinix.forge.user.dto.UpdateUserRequest;
import com.clinix.forge.user.dto.UserResponse;
import com.clinix.forge.user.entity.UserEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
        // Set manually in service
    UserEntity toEntity(CreateUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
        // Set manually in service
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget UserEntity entity);

    UserResponse toResponse(UserEntity entity);
}
