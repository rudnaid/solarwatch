package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.model.*;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunriseSunsetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            City city = objectMapper.convertValue(geoCodingResponse[0], City.class);
            return cityRepository.save(city);
        } catch (Exception e) {
            throw new RuntimeException("Error mapping GeoCodingResponse to City", e);
        }
    }

    /**
     * Call external API to get sunrise/sunset data for a City and save the results to database.
     *
     * @param city City entity.
     * @param tzid Time Zone ID.
     * @param date Date of the requested sunrise/sunset data.
     * @param formatted Date formatting options.
     * @return SunriseSunset entity.
     */

    private SunriseSunset createSunriseSunsetForCity(City city, String tzid, String date, int formatted) {
        SunriseSunsetResponseDTO response = sunriseSunsetApiClient.getSunriseSunsetByCoordinates(city.getLat(), city.getLon(), date, tzid, formatted);
        SunriseSunsetDTO times = response.results();

        SunriseSunset sunriseSunset = new SunriseSunset();
        sunriseSunset.setCity(city);
        sunriseSunset.setSunrise(times.sunrise());
        sunriseSunset.setSunset(times.sunset());

        return sunriseSunsetRepository.save(sunriseSunset);
    }

    /**
     * Convert City and SunriseSunset entities to a response DTO.
     *
     * @param city City entity.
     * @param sunriseSunset SunriseSunset entity.
     * @return CityResponseDTO, containing the cities name, country and sunrise/sunset data.
     */

    private CityResponseDTO convertToCityResponseDTO(City city, SunriseSunset sunriseSunset) {
        CityResponseDTO cityResponseDTO = new CityResponseDTO();
        cityResponseDTO.setName(city.getName());
        cityResponseDTO.setCountry(city.getCountry());

        SunriseSunsetDTO sunriseSunsetDTO = convertToSunriseSunsetDTO(sunriseSunset);
        cityResponseDTO.setSunriseSunset(sunriseSunsetDTO);
        return cityResponseDTO;
    }

    /**
     * Convert a SunriseSunset entity to a SunriseSunsetDTO.
     *
     * @param sunriseSunset SunriseSunset entity.
     * @return SunriseSunsetDTO, containing the sunrise and sunset data.
     */

    private SunriseSunsetDTO convertToSunriseSunsetDTO(SunriseSunset sunriseSunset) {
        return new SunriseSunsetDTO(sunriseSunset.getSunrise(), sunriseSunset.getSunset());
    }

    /**
     * Get sunrise/sunset data on a given date for a city. Searches database for existing data and calls external API if no data was found.
     *
     * @param cityName Name of the city.
     * @param date Date of sunrise/sunset.
     * @param tzid Time Zone ID.
     * @param formatted Date formatting options
     * @return CityResponseDTO, containing the cities name, country and sunrise/sunset data.
     */

    public CityResponseDTO getSolarTimes(String cityName, LocalDate date, String tzid, int formatted) {
        City city = getCityByName(cityName);

        Optional<SunriseSunset> sunriseSunset = sunriseSunsetRepository.findByCityAndDate(city, date);

        if (sunriseSunset.isPresent()) {
            return convertToCityResponseDTO(city, sunriseSunset.get());
        } else {
            String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            SunriseSunset newSunriseSunset = createSunriseSunsetForCity(city, tzid, dateString, formatted);
            return convertToCityResponseDTO(city, newSunriseSunset);
        }
    }
}
