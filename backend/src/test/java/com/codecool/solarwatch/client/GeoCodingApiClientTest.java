package com.codecool.solarwatch.client;

import com.codecool.solarwatch.model.dto.GeoCodingResponseDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class GeoCodingApiClientTest {

    @Mock
    private WebClient webClient;

    private MockWebServer mockWebServer;

    private GeoCodingApiClient geoCodingApiClient;

    private final String baseUrl = "/";

    private final String apiKey = "testApiKey";

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
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getGeoCoordinatesForCity_GivenValidCity_ReturnsCorrectCoordinates() {
        String city = "TestCity";
        String jsonResponse = "[{\"name\":\"TestCity\",\"lat\":52.52,\"lon\":13.405,\"country\":\"DE\",\"state\":\"TestCity\"}]";

        mockWebServer.enqueue(new MockResponse().setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        GeoCodingResponseDTO[] result = geoCodingApiClient.getGeoCoordinatesForCity(city);

        assertNotNull(result);
        assertEquals("TestCity", result[0].name());
        assertEquals(52.52, result[0].lat());
        assertEquals(13.405, result[0].lon());
        assertEquals("DE", result[0].country());
        assertEquals("TestCity", result[0].state());
    }

    @Test
    void getGeoCoordinatesForCity_GivenInvalidCity_ReturnsEmptyArray() {
        String city = "Invalid";
        String jsonResponse = "[]";

        mockWebServer.enqueue(new MockResponse().setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        GeoCodingResponseDTO[] result = geoCodingApiClient.getGeoCoordinatesForCity(city);

        assertNotNull(result);
        assertEquals(0, result.length);
    }
}
