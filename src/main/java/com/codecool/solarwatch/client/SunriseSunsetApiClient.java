package com.codecool.solarwatch.client;

import com.codecool.solarwatch.exception.InvalidDateException;
import com.codecool.solarwatch.exception.InvalidRequestException;
import com.codecool.solarwatch.exception.InvalidTimezoneException;
import com.codecool.solarwatch.exception.UnknownApiErrorException;
import com.codecool.solarwatch.model.SolarTimesResponse;
import org.springframework.http.HttpStatus;
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
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {

                String responseBody = ex.getResponseBodyAsString();

                if (responseBody.contains("\"status\":\"INVALID_DATE\"")) {
                    throw new InvalidDateException();
                } else if (responseBody.contains("\"status\":\"INVALID_TZID\"")) {
                    throw new InvalidTimezoneException();
                } else if (responseBody.contains("\"status\":\"INVALID_REQUEST\"")) {
                    throw new InvalidRequestException();
                } else if (responseBody.contains("\"status\":\"UNKNOWN_ERROR\"")) {
                    throw new UnknownApiErrorException();
                }
            }

            throw ex;
        }
    }
}
