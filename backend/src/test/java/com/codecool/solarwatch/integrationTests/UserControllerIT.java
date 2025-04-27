package com.codecool.solarwatch.integrationTests;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.model.entity.Role;
import com.codecool.solarwatch.model.entity.UserEntity;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private GeoCodingApiClient geoCodingApiClient;

    @MockBean
    private SunriseSunsetApiClient sunriseSunsetApiClient;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void register_WithValidCredentials_CreatesUser() throws Exception {
        String registerRequest = """
                {
                "username": "newUser",
                "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isOk());

        UserEntity savedUser = userRepository.findByUsername("newUser")
                .orElseThrow(() -> new AssertionError("User not found after registration"));
        
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        assertTrue(savedUser.getRoles().contains(Role.ROLE_USER));
        assertEquals(1, savedUser.getRoles().size());
    }

    @Test
    void register_WithExistingUsername_ReturnsBadRequest() throws Exception {
        UserEntity existingUser = new UserEntity();
        existingUser.setUsername("existingUser");
        existingUser.setPassword(passwordEncoder.encode("password"));
        existingUser.setRoles(Set.of(Role.ROLE_USER));
        userRepository.save(existingUser);

        String registerRequest = """
                {
                "username": "existingUser",
                "password": "newPassword"
                }
                """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isBadRequest());

        assertEquals(1, userRepository.count());
    }

    @Test
    void login_WithValidCredentials_ReturnsJwtToken() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("loginUser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRoles(Set.of(Role.ROLE_USER));
        userRepository.save(user);

        String loginRequest = """
                {
                "username": "loginUser",
                "password": "password123"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").exists())
                .andExpect(jsonPath("$.username").value("loginUser"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(responseContent, JwtResponse.class);
        
        assertNotNull(jwtResponse.jwtToken());
        assertEquals("loginUser", jwtResponse.username());
        assertEquals(1, jwtResponse.roles().size());
        assertEquals("ROLE_USER", jwtResponse.roles().get(0));
    }

    @Test
    void login_WithInvalidUsername_ReturnsUnauthorized() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("realUser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRoles(Set.of(Role.ROLE_USER));
        userRepository.save(user);

        String loginRequest = """
                {
                "username": "fakeUser",
                "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithInvalidPassword_ReturnsUnauthorized() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("passwordUser");
        user.setPassword(passwordEncoder.encode("correctPassword"));
        user.setRoles(Set.of(Role.ROLE_USER));
        userRepository.save(user);

        String loginRequest = """
                {
                "username": "passwordUser",
                "password": "wrongPassword"
                }
                """;

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }
} 