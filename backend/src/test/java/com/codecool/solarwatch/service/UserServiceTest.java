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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

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
        UserRequest request = new UserRequest("newUser", "password");
        UserEntity savedUser = new UserEntity();
        savedUser.setUsername("newUser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRoles(Set.of(Role.ROLE_USER));
        
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        userService.createUser(request);
        
        verify(userRepository).findByUsername("newUser");
        verify(encoder).encode("password");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_shouldThrow_whenUsernameAlreadyExists() {
        UserRequest request = new UserRequest("existingUser", "password");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request);
        });
        
        verify(userRepository).findByUsername("existingUser");
        verify(encoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void authenticateUser_shouldReturnJwtResponse() {
        UserRequest loginRequest = new UserRequest("existingUser", "password");

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
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
        verify(authentication).getPrincipal();
        verify(userDetails).getUsername();
        verify(userDetails).getAuthorities();
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
        
        verify(userRepository).findByUsername("existingUser");
        verify(userRepository).save(userEntity);
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
        
        verify(userRepository).findByUsername("existingAdmin");
        verify(userRepository).save(userEntity);
    }
}
