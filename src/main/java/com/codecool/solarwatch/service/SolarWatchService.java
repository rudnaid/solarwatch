package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.exception.InvalidCityException;
import com.codecool.solarwatch.model.*;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunriseSunsetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Transactional
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

    private SunriseSunset createSunriseSunsetForCity(City city, String tzid, String date, int formatted) {
        SunriseSunsetResponseDTO response = sunriseSunsetApiClient.getSunriseSunsetByCoordinates(city.getLat(), city.getLon(), tzid, date, formatted);
        SunriseSunsetDTO times = response.results();

        SunriseSunset sunriseSunset = new SunriseSunset();
        sunriseSunset.setCity(city);
        sunriseSunset.setSunrise(times.sunrise());
        sunriseSunset.setSunset(times.sunset());

        return sunriseSunsetRepository.save(sunriseSunset);
    }

    public SunriseSunset getSolarTimes(String cityName, String dateString, String tzid, int formatted) {
        City city = getCityByName(cityName);
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);

        Optional<SunriseSunset> sunriseSunset = sunriseSunsetRepository.findByCityAndCreatedAt(city, date);

        if (sunriseSunset.isEmpty()) {
            SunriseSunset newSunriseSunset = createSunriseSunsetForCity(city, tzid, dateString, formatted);
            sunriseSunset = Optional.of(sunriseSunsetRepository.save(newSunriseSunset));
        }

        return sunriseSunset.get();
    }
}
