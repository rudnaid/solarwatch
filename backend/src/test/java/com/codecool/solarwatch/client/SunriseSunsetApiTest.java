package com.codecool.solarwatch.client;

import com.codecool.solarwatch.model.dto.SunriseSunsetDTO;
import com.codecool.solarwatch.model.dto.SunriseSunsetResponseDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SunriseSunsetApiTest {

    @Mock
    private WebClient webClient;

    private MockWebServer mockWebServer;

    private final String baseUrl = "testUrl";

    @InjectMocks
    private SunriseSunsetApiClient sunriseSunsetApiClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        sunriseSunsetApiClient = new SunriseSunsetApiClient(webClient, baseUrl);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getSunriseSunsetByCoordinates_GivenValidGeoCoordinates_ReturnsSunriseSunset() {
        double lat = 52.52;
        double lng = 13.405;
        String date = "2025-03-15";
        String tzid = "Europe/Berlin";
        int formatted = 0;

        String jsonResponse = "{"
                + "\"results\": {"
                + "\"sunrise\": \"2025-03-15T06:12:00+00:00\","
                + "\"sunset\": \"2025-03-15T18:35:00+00:00\""
                + "}"
                + "}";

        mockWebServer.enqueue(new MockResponse().setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        SunriseSunsetResponseDTO response = sunriseSunsetApiClient.getSunriseSunsetByCoordinates(lat, lng, date, tzid, formatted);
        SunriseSunsetDTO result = response.results();

        ZonedDateTime expectedSunrise = ZonedDateTime.parse("2025-03-15T06:12:00+00:00");
        ZonedDateTime expectedSunset = ZonedDateTime.parse("2025-03-15T18:35:00+00:00");

        assertNotNull(response);
        assertEquals(expectedSunrise, result.sunrise());
        assertEquals(expectedSunset, result.sunset());
    }

    @Test
    void getSunriseSunsetByCoordinates_GivenOutOfRangeGeoCoordinates_ThrowsException() {
        double lat = 100.0;
        double lng = 200.0;
        String date = "2025-03-15";
        String tzid = "Europe/Berlin";
        int formatted = 0;

        String jsonResponse = "{"
                + "\"error\": \"Invalid coordinates\""
                + "}";

        mockWebServer.enqueue(new MockResponse().setResponseCode(400)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        assertThrows(ResponseStatusException.class, () ->
                sunriseSunsetApiClient.getSunriseSunsetByCoordinates(lat, lng, date, tzid, formatted)
        );
    }
}
