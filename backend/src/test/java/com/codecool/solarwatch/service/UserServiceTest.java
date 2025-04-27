package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.entity.Role;
import com.codecool.solarwatch.model.entity.UserEntity;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.model.payload.UserRequest;
import com.codecool.solarwatch.repository.UserRepository;
import com.codecool.solarwatch.security.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;
    @Mock private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        UserRequest request = new UserRequest();
        request.setUsername("newUser");
        request.setPassword("password");

        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity());

        userService.createUser(request);
    }

    @Test
    void createUser_shouldThrow_whenUsernameAlreadyExists() {
        UserRequest request = new UserRequest();
        request.setUsername("existingUser");
        request.setPassword("password");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request);
        });
    }

    @Test
    void authenticateUser_shouldReturnJwtResponse() {
        UserRequest loginRequest = new UserRequest();
        loginRequest.setUsername("existingUser");
        loginRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwtToken");

        User userDetails = mock(User.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("existingUser");
        when(userDetails.getAuthorities()).thenReturn(Set.of(() -> "ROLE_USER"));

        JwtResponse response = userService.authenticateUser(loginRequest);

        assertEquals("jwtToken", response.jwtToken());
        assertEquals("existingUser", response.username());
        assertTrue(response.roles().contains("ROLE_USER"));
    }


    @Test
    void promoteToAdmin_shouldPromoteUserSuccessfully() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("existingUser");
        userEntity.setRoles(new HashSet<>(Set.of(Role.ROLE_USER)));

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        userService.promoteToAdmin("existingUser");

        assertTrue(userEntity.getRoles().contains(Role.ROLE_ADMIN));
    }

    @Test
    void promoteToAdmin_shouldNotChangeRoles_whenAlreadyAdmin() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("existingAdmin");
        userEntity.setRoles(new HashSet<>(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN)));

        when(userRepository.findByUsername("existingAdmin")).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        userService.promoteToAdmin("existingAdmin");

        assertTrue(userEntity.getRoles().contains(Role.ROLE_ADMIN));
    }
}
