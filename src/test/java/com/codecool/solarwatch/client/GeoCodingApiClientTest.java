package com.codecool.solarwatch.client;

import com.codecool.solarwatch.exception.InvalidCityException;
import com.codecool.solarwatch.model.GeoCoordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeoCodingApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private GeoCodingApiClient geoCodingApiClient;

    private final String apiKey = "testApiKey";

    @BeforeEach
    void setUp() {
        geoCodingApiClient = new GeoCodingApiClient(restTemplate, apiKey);
    }

    @Test
    void getGeoCoordinatesForCity_GivenValidCity_ReturnsCorrectCoordinates() {
        // Arrange
        String city = "Berlin";
        String url = String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s", city, apiKey);
        GeoCoordinates[] mockResponse = { new GeoCoordinates(52.52, 13.405) };

        when(restTemplate.getForObject(url, GeoCoordinates[].class)).thenReturn(mockResponse);

        // Act
        GeoCoordinates result = geoCodingApiClient.getGeoCoordinatesForCity(city);

        // Assert
        assertNotNull(result);
        assertEquals(52.52, result.lat());
        assertEquals(13.405, result.lon());
    }

    @Test
    void getGeoCoordinatesForCity_GivenInvalidCity_ApiReturnsEmpty_ThrowsInvalidCityException() {
        String city = "InvalidCity";
        String url = String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s", city, apiKey);

        when(restTemplate.getForObject(url, GeoCoordinates[].class)).thenReturn(new GeoCoordinates[0]);

        assertThrows(InvalidCityException.class, () -> geoCodingApiClient.getGeoCoordinatesForCity(city));
    }

    @Test
    void getGeoCoordinatesForCity_GivenInvalidCity_ApiReturnsNull_ThrowsInvalidCityException() {
        String city = "UnknownCity";
        String url = String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s", city, apiKey);

        when(restTemplate.getForObject(url, GeoCoordinates[].class)).thenReturn(null);

        assertThrows(InvalidCityException.class, () -> geoCodingApiClient.getGeoCoordinatesForCity(city));
    }
}