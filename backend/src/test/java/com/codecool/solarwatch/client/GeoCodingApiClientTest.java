package com.codecool.solarwatch.client;

import com.codecool.solarwatch.model.dto.GeoCodingResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeoCodingApiClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private GeoCodingApiClient geoCodingApiClient;

    private final String baseUrl = "http://test-api.com";
    private final String apiKey = "testApiKey";

    @BeforeEach
    void setUp() {
        geoCodingApiClient = new GeoCodingApiClient(webClient, baseUrl, apiKey);
    }

    @Test
    void getGeoCoordinatesForCity_GivenValidCity_ReturnsCorrectCoordinates() {
        String city = "TestCity";
        GeoCodingResponseDTO[] expectedResponse = new GeoCodingResponseDTO[]{
                new GeoCodingResponseDTO("TestCity", 52.52, 13.405, "DE", "TestCity")
        };
        
        String expectedUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .build()
                .toUriString();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GeoCodingResponseDTO[].class)).thenReturn(Mono.just(expectedResponse));

        GeoCodingResponseDTO[] result = geoCodingApiClient.getGeoCoordinatesForCity(city);

        assertNotNull(result);
        assertEquals("TestCity", result[0].name());
        assertEquals(52.52, result[0].lat());
        assertEquals(13.405, result[0].lon());
        assertEquals("DE", result[0].country());
        assertEquals("TestCity", result[0].state());
        
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(expectedUrl);
        verify(requestHeadersSpec).accept(MediaType.APPLICATION_JSON);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(GeoCodingResponseDTO[].class);
    }

    @Test
    void getGeoCoordinatesForCity_GivenInvalidCity_ReturnsEmptyArray() {
        String city = "Invalid";
        GeoCodingResponseDTO[] emptyResponse = new GeoCodingResponseDTO[0];
        
        String expectedUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .build()
                .toUriString();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GeoCodingResponseDTO[].class)).thenReturn(Mono.just(emptyResponse));

        GeoCodingResponseDTO[] result = geoCodingApiClient.getGeoCoordinatesForCity(city);

        assertNotNull(result);
        assertEquals(0, result.length);
        
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(expectedUrl);
        verify(requestHeadersSpec).accept(MediaType.APPLICATION_JSON);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(GeoCodingResponseDTO[].class);
    }
}
