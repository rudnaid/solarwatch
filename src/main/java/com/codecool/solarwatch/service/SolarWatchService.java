package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.exception.InvalidCityException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.GeoCodingResponseDTO;
import com.codecool.solarwatch.model.SolarTimesResponse;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunriseSunsetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolarWatchService {
    private final GeoCodingApiClient geoCodingApiClient;
    private final SunriseSunsetApiClient sunriseSunsetApiClient;
    private final CityRepository cityRepository;
    private final SunriseSunsetRepository sunriseSunsetRepository;

    @Autowired
    public SolarWatchService(GeoCodingApiClient geoCodingApiClient, SunriseSunsetApiClient sunriseSunsetApiClient, CityRepository cityRepository, SunriseSunsetRepository sunriseSunsetRepository) {
        this.geoCodingApiClient = geoCodingApiClient;
        this.sunriseSunsetApiClient = sunriseSunsetApiClient;
        this.cityRepository = cityRepository;
        this.sunriseSunsetRepository = sunriseSunsetRepository;
    }

    private City getCityByName(String cityName) {
        City city = cityRepository.findByName(cityName).orElse(null);

        if (city == null) {
            city = createCityFromGeoCodingResponse(cityName);
        }
        return city;
    }

    private City createCityFromGeoCodingResponse(String cityName) {
        GeoCodingResponseDTO[] geoCodingResponse = geoCodingApiClient.getGeoCoordinatesForCity(cityName);

        if (geoCodingResponse == null || geoCodingResponse.length == 0) {
            throw new InvalidCityException();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            City city = objectMapper.convertValue(geoCodingResponse[0], City.class);
            return cityRepository.save(city);
        } catch (Exception e) {
            throw new RuntimeException("Error mapping GeoCodingResponse to City", e);
        }
    }

    public SolarTimesResponse getSolarTimes(String cityName, String date, String tzid, int formatted) {
        City city = getCityByName(cityName);

        return sunriseSunsetApiClient.getSunriseSunsetByCoordinates(city.getLat(), city.getLon(), date, tzid, formatted);
    }
}
