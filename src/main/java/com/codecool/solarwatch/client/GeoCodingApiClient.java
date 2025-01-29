package com.codecool.solarwatch.client;

import com.codecool.solarwatch.exception.InvalidCityException;
import com.codecool.solarwatch.model.GeoCoordinates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GeoCodingApiClient {

    private final String apiKey;
    private final RestTemplate restTemplate;

    public GeoCodingApiClient(RestTemplate restTemplate, @Value("${api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public GeoCoordinates getGeoCoordinatesForCity(String city) {
        String url = String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s", city, apiKey);

        GeoCoordinates[] response = restTemplate.getForObject(url, GeoCoordinates[].class);

        if (response == null || response.length == 0) {
            throw new InvalidCityException();
        }

        return response[0];
    }
}
