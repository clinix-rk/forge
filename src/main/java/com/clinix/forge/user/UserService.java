package com.clinix.forge.user;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.user.dto.CreateUserRequest;
import com.clinix.forge.user.dto.UpdateUserRequest;
import com.clinix.forge.user.dto.UserResponse;
import com.clinix.forge.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with username: {}", request.username());

        if (userRepository.existsByUsername(request.username())) {
            log.warn("Username already exists: {}", request.username());
            throw new DuplicateResourceException("Username " + request.username() + " is already taken");
        }

        UserEntity userEntity = userMapper.toEntity(request);
        userEntity.setPassword(passwordEncoder.encode(request.password()));

        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int pageNo, int pageSize) {
        log.debug("Fetching users - PageNo: {}, PageSize: {}", pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<UserEntity> userPage = userRepository.findAll(pageRequest);

        return userPage.map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with ID: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return userMapper.toResponse(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponse updateUserById(Long id, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // If username changes, check uniqueness
        if (!user.getUsername().equals(request.username()) && userRepository.existsByUsername(request.username())) {
            log.warn("Username already exists during update: {}", request.username());
            throw new DuplicateResourceException("Username " + request.username() + " is already taken");
        }

        userMapper.updateEntityFromRequest(request, user);

        // Update password if provided
        if (request.password() != null && !request.password().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        UserEntity updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUserById(Long id) {
        log.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User not found for deletion with ID: {}", id);
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully: {}", id);
    }
}
