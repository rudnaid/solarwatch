package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.model.GeoCoordinates;
import com.codecool.solarwatch.model.SolarTimesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolarWatchService {
    private final GeoCodingApiClient geoCodingApiClient;
    private final SunriseSunsetApiClient sunriseSunsetApiClient;

    @Autowired
    public SolarWatchService(GeoCodingApiClient geoCodingApiClient, SunriseSunsetApiClient sunriseSunsetApiClient) {
        this.geoCodingApiClient = geoCodingApiClient;
        this.sunriseSunsetApiClient = sunriseSunsetApiClient;
    }

    public SolarTimesResponse getSolarTimes(String city, String date, String tzid, int formatted) {
        GeoCoordinates coordinates = geoCodingApiClient.getGeoCoordinatesForCity(city);
        return sunriseSunsetApiClient.getSunriseSunsetByCoordinates(coordinates.lat(), coordinates.lon(), date, tzid, formatted);
    }
}
