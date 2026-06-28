package com.clinix.forge.user;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.user.dto.CreateUserRequest;
import com.clinix.forge.user.dto.UpdateUserRequest;
import com.clinix.forge.user.dto.UserResponse;
import com.clinix.forge.user.entity.Role;
import com.clinix.forge.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private UserResponse userResponse;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    public void setUp() {
        userEntity = UserEntity.builder()
                .username("test_user")
                .password("encoded_pass")
                .role(Role.ADMIN)
                .build();
        userEntity.setId(1L);

        userResponse = new UserResponse(1L, "test_user", Role.ADMIN, Instant.now(), Instant.now());
        createRequest = new CreateUserRequest("test_user", "raw_pass", Role.ADMIN);
        updateRequest = new UpdateUserRequest("test_user_updated", "new_raw_pass", Role.ADMIN);
    }

    @Test
    public void createUser_Success() {
        when(userRepository.existsByUsername(createRequest.username())).thenReturn(false);
        when(userMapper.toEntity(createRequest)).thenReturn(userEntity);
        when(passwordEncoder.encode(createRequest.password())).thenReturn("encoded_pass");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.createUser(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("test_user");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void createUser_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername(createRequest.username())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(createRequest));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    public void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void getAllUsers_Paginated() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<UserEntity> page = new PageImpl<>(List.of(userEntity));
        when(userRepository.findAll(pageRequest)).thenReturn(page);
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

        PaginatedPayload<UserResponse> result = userService.getAllUsers(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().username()).isEqualTo("test_user");
    }

    @Test
    public void updateUser_Success_WithNewPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByUsername(updateRequest.username())).thenReturn(false);
        when(passwordEncoder.encode(updateRequest.password())).thenReturn("new_encoded_pass");
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.updateUserById(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode(updateRequest.password());
        verify(userRepository).save(userEntity);
    }

    @Test
    public void updateUser_Success_WithoutPasswordChange() {
        UpdateUserRequest updateNoPass = new UpdateUserRequest("test_user_updated", null, Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByUsername(updateNoPass.username())).thenReturn(false);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.updateUserById(1L, updateNoPass);

        assertThat(result).isNotNull();
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(userEntity);
    }

    @Test
    public void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    public void deleteUser_NotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(1L));
        verify(userRepository, never()).deleteById(1L);
    }
}
