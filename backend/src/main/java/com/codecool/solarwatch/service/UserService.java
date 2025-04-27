package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.entity.Role;
import com.codecool.solarwatch.model.entity.UserEntity;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.model.payload.UserRequest;
import com.codecool.solarwatch.repository.UserRepository;
import com.codecool.solarwatch.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
    }

    /**
     * Registers a new user. Encodes the password and assigns the default user role.
     *
     * @param registerRequest UserRequest containing username and password.
     * @throws IllegalArgumentException if the username already exists.
     */
    public void createUser(UserRequest registerRequest) {
        Optional<UserEntity> existingUser = userRepository.findByUsername(registerRequest.getUsername());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username: " + registerRequest.getUsername() + " already exists");
        }

        UserEntity user = new UserEntity();

        user.setUsername(registerRequest.getUsername());
        user.setPassword(encoder.encode(registerRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        user.setRoles(roles);

        userRepository.save(user);
    }

    /**
     * Authenticates the user and generates a JWT token.
     *
     * @param loginRequest UserRequest containing login credentials.
     * @return JwtResponse containing JWT token, username, and roles.
     */
    public JwtResponse authenticateUser(UserRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()));

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new JwtResponse(jwt, userDetails.getUsername(), roles);
    }

    /**
     * Promotes an existing user to an admin by adding the ROLE_ADMIN to their roles.
     *
     * @param username the username of the user to promote.
     * @throws NoSuchElementException if the user is not found.
     */
    public void promoteToAdmin(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User: " + username + " not found"));

        Set<Role> newRoles = new HashSet<>(userEntity.getRoles());
        newRoles.add(Role.ROLE_ADMIN);
        userEntity.setRoles(newRoles);

        userRepository.save(userEntity);
    }
}
