package com.codecool.solarwatch.client;

import com.codecool.solarwatch.model.dto.SunriseSunsetDTO;
import com.codecool.solarwatch.model.dto.SunriseSunsetResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SunriseSunsetApiTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private SunriseSunsetApiClient sunriseSunsetApiClient;

    private final String baseUrl = "testUrl";

    @BeforeEach
    void setUp() {
        sunriseSunsetApiClient = new SunriseSunsetApiClient(webClient, baseUrl);
    }

    @Test
    void getSunriseSunsetByCoordinates_GivenValidGeoCoordinates_ReturnsSunriseSunset() {
        double lat = 52.52;
        double lng = 13.405;
        String date = "2025-03-15";
        String tzid = "Europe/Berlin";
        int formatted = 0;

        ZonedDateTime expectedSunrise = ZonedDateTime.parse("2025-03-15T06:12:00+00:00");
        ZonedDateTime expectedSunset = ZonedDateTime.parse("2025-03-15T18:35:00+00:00");
        
        SunriseSunsetDTO sunriseSunsetDTO = new SunriseSunsetDTO(expectedSunrise, expectedSunset);
        SunriseSunsetResponseDTO expectedResponse = new SunriseSunsetResponseDTO(sunriseSunsetDTO);

        String expectedUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("lat", lat)
                .queryParam("lng", lng)
                .queryParam("date", date)
                .queryParam("tzid", tzid)
                .queryParam("formatted", formatted)
                .build()
                .toUriString();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(SunriseSunsetResponseDTO.class)).thenReturn(Mono.just(expectedResponse));

        SunriseSunsetResponseDTO response = sunriseSunsetApiClient.getSunriseSunsetByCoordinates(lat, lng, date, tzid, formatted);
        SunriseSunsetDTO result = response.results();

        assertNotNull(response);
        assertEquals(expectedSunrise, result.sunrise());
        assertEquals(expectedSunset, result.sunset());
        
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(expectedUrl);
        verify(requestHeadersSpec).accept(MediaType.APPLICATION_JSON);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(SunriseSunsetResponseDTO.class);
    }

    @Test
    void getSunriseSunsetByCoordinates_GivenOutOfRangeGeoCoordinates_ThrowsException() {
        double lat = 100.0;
        double lng = 200.0;
        String date = "2025-03-15";
        String tzid = "Europe/Berlin";
        int formatted = 0;

        String expectedUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("lat", lat)
                .queryParam("lng", lng)
                .queryParam("date", date)
                .queryParam("tzid", tzid)
                .queryParam("formatted", formatted)
                .build()
                .toUriString();

        WebClientResponseException exception = WebClientResponseException.create(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            null,
            "{\"error\": \"Invalid coordinates\"}".getBytes(),
            null
        );
        
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(SunriseSunsetResponseDTO.class)).thenReturn(Mono.error(exception));

        assertThrows(ResponseStatusException.class, () ->
                sunriseSunsetApiClient.getSunriseSunsetByCoordinates(lat, lng, date, tzid, formatted)
        );
        
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(expectedUrl);
        verify(requestHeadersSpec).accept(MediaType.APPLICATION_JSON);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(SunriseSunsetResponseDTO.class);
    }
}
