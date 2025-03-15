package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SunriseSunset;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunriseSunsetRepository;
import jakarta.transaction.Transactional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class SolarWatchServiceTest {

    @InjectMocks
    private GeoCodingApiClient geoCodingApiClient;

    @Autowired
    private SunriseSunsetApiClient sunriseSunsetApiClient;

    private String apiKey = "testApiKey";

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private SunriseSunsetRepository sunriseSunsetRepository;

    @Autowired
    private SolarWatchService solarWatchService;

    private MockWebServer mockWebServer;

    @Mock
    private WebClient webClient;

    private String baseUrl = "/";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = Mockito.mock(WebClient.class);

        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        geoCodingApiClient = new GeoCodingApiClient(webClient, baseUrl, apiKey);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testUpdateCity() {
        City testCity = new City();
        testCity.setName("Test City");
        testCity.setCountry("Test Country");
        testCity.setLat(12.34);
        testCity.setLon(56.78);
        cityRepository.save(testCity);

        String updatedName = "Updated Test City";
        String updatedCountry = "Updated Test Country";

        testCity.setName(updatedName);
        testCity.setCountry(updatedCountry);
        cityRepository.save(testCity);

        City updatedCity = cityRepository.findByName(updatedName).orElseThrow();
        assertEquals(updatedName, updatedCity.getName());
        assertEquals(updatedCountry, updatedCity.getCountry());
    }

    @Test
    void testUpdateSunriseSunset() {
        City testCity = new City();
        testCity.setName("Test City");
        testCity.setCountry("Test Country");
        testCity.setLat(12.34);
        testCity.setLon(56.78);
        cityRepository.save(testCity);

        SunriseSunset testSunriseSunset = new SunriseSunset();
        testSunriseSunset.setCity(testCity);
        testSunriseSunset.setSunrise(ZonedDateTime.parse("2025-03-15T06:12:00+00:00"));
        testSunriseSunset.setSunset(ZonedDateTime.parse("2025-03-15T18:12:00+00:00"));
        sunriseSunsetRepository.save(testSunriseSunset);

        ZonedDateTime updatedSunrise = ZonedDateTime.parse("2025-03-15T07:12:00+00:00");
        ZonedDateTime updatedSunset = ZonedDateTime.parse("2025-03-15T19:12:00+00:00");

        testSunriseSunset.setSunrise(updatedSunrise);
        testSunriseSunset.setSunset(updatedSunset);
        sunriseSunsetRepository.save(testSunriseSunset);

        SunriseSunset updatedSunriseSunset = sunriseSunsetRepository.findById(testSunriseSunset.getId()).orElseThrow();
        assertEquals(updatedSunrise, updatedSunriseSunset.getSunrise());
        assertEquals(updatedSunset, updatedSunriseSunset.getSunset());
    }

    @Test
    void testCreateCityFromGeoCodingResponse() {
        String cityName = "TestCity";
        String jsonResponse = """
                [
                    {
                        "name": "TestCity",
                        "lat": 1.1,
                        "lon": 1.1,
                        "country": "TestCountry",
                        "state": "TestState",
                    }
                ]
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        City result = solarWatchService.createCityFromGeoCodingResponse(cityName);

        assertEquals("TestCity", result.getName());
        assertEquals("TestCountry", result.getCountry());
        assertEquals("TestState", result.getState());
        assertEquals(1.1, result.getLat());
        assertEquals(1.1, result.getLon());
    }
}