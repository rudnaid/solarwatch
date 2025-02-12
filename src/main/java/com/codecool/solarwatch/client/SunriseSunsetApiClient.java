package com.codecool.solarwatch.client;

import com.codecool.solarwatch.exception.UnknownApiErrorException;
import com.codecool.solarwatch.model.SolarTimesResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class SunriseSunsetApiClient {
    private final RestTemplate restTemplate;

    public SunriseSunsetApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SolarTimesResponse getSunriseSunsetByCoordinates(double lat, double lng, String date, String tzid, int formatted) {
        String url = String.format("https://api.sunrise-sunset.org/json?lat=%f&lng=%f&date=%s&tzid=%s&formatted=%d", lat, lng, date, tzid, formatted);

        try {
            return restTemplate.getForObject(url, SolarTimesResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new UnknownApiErrorException();
        }
    }
}
