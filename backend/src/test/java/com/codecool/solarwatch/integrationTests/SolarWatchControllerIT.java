package com.codecool.solarwatch.integrationTests;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.model.dto.GeoCodingResponseDTO;
import com.codecool.solarwatch.model.dto.SunriseSunsetDTO;
import com.codecool.solarwatch.model.dto.SunriseSunsetResponseDTO;
import com.codecool.solarwatch.model.entity.Role;
import com.codecool.solarwatch.model.entity.UserEntity;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunriseSunsetRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class SolarWatchControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private SunriseSunsetRepository sunriseSunsetRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private GeoCodingApiClient geoCodingApiClient;

    @MockBean
    private SunriseSunsetApiClient sunriseSunsetApiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity testUser;
    private String jwtToken;

    @BeforeEach
    public void setup() throws Exception {
        sunriseSunsetRepository.deleteAll();
        cityRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new UserEntity();
        testUser.setUsername("testUser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRoles(Set.of(Role.ROLE_USER));
        userRepository.save(testUser);

        String userCredentials = """
                {
                "username": "testUser",
                "password": "password"
                }
                """;

        String responseContent = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userCredentials))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JwtResponse jwtResponse = objectMapper.readValue(responseContent, JwtResponse.class);
        jwtToken = jwtResponse.jwtToken();
    }

    @AfterEach
    public void tearDown() {
        sunriseSunsetRepository.deleteAll();
        cityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getSolarTimes_ReturnsExpectedData() throws Exception {
        String testCity = "Budapest";
        double testLat = 47.497913;
        double testLon = 19.040236;
        String testCountry = "HU";
        String testState = "HU";
        String testTzid = "UTC";
        int testFormatted = 0;

        ZonedDateTime sunrise = ZonedDateTime.parse("2024-04-22T06:00:00Z");
        ZonedDateTime sunset = ZonedDateTime.parse("2024-04-22T18:00:00Z");

        GeoCodingResponseDTO geoCodingResponseDTO = new GeoCodingResponseDTO(testCity, testLat, testLon, testCountry, testState);

        when(geoCodingApiClient.getGeoCoordinatesForCity(eq(testCity)))
                .thenReturn(new GeoCodingResponseDTO[]{geoCodingResponseDTO});

        SunriseSunsetDTO sunriseSunsetDTO = new SunriseSunsetDTO(sunrise, sunset);
        SunriseSunsetResponseDTO responseDTO = new SunriseSunsetResponseDTO(sunriseSunsetDTO);

        when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(
                eq(testLat),
                eq(testLon),
                anyString(),
                eq(testTzid),
                eq(testFormatted)))
                .thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/solarwatch/times")
                        .param("city", testCity)
                        .param("tzid", testTzid)
                        .param("formatted", String.valueOf(testFormatted))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testCity))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value(testCountry))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sunriseSunset.sunrise").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sunriseSunset.sunset").exists());
    }

    @Test
    void getSolarTimes_WithSpecifiedDate_UsesCorrectDate() throws Exception {
        String testCity = "Budapest";
        double testLat = 47.497913;
        double testLon = 19.040236;
        String testCountry = "HU";
        String testState = "HU";
        String testDate = "2024-05-15";
        String testTzid = "Europe/Budapest";
        int testFormatted = 0;

        ZonedDateTime sunrise = ZonedDateTime.parse("2024-05-15T05:30:00+02:00[Europe/Budapest]");
        ZonedDateTime sunset = ZonedDateTime.parse("2024-05-15T20:15:00+02:00[Europe/Budapest]");

        GeoCodingResponseDTO geoCodingResponseDTO = new GeoCodingResponseDTO(testCity, testLat, testLon, testCountry, testState);

        when(geoCodingApiClient.getGeoCoordinatesForCity(eq(testCity)))
                .thenReturn(new GeoCodingResponseDTO[]{geoCodingResponseDTO});

        SunriseSunsetDTO sunriseSunsetDTO = new SunriseSunsetDTO(sunrise, sunset);
        SunriseSunsetResponseDTO responseDTO = new SunriseSunsetResponseDTO(sunriseSunsetDTO);

        when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(
                eq(testLat),
                eq(testLon),
                eq(testDate),
                eq(testTzid),
                eq(testFormatted)))
                .thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/solarwatch/times")
                        .param("city", testCity)
                        .param("date", testDate)
                        .param("tzid", testTzid)
                        .param("formatted", String.valueOf(testFormatted))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testCity))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value(testCountry))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sunriseSunset.sunrise").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sunriseSunset.sunset").exists());
    }
}