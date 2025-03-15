package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.model.dto.*;
import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SunriseSunset;
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
import java.util.NoSuchElementException;
import java.util.Optional;

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

    /**
     * Get City entity based on name of city. Searches database for existing data, calls external API if no data was found.
     *
     * @param cityName Name of the city.
     * @return City entity containing name, country, state, latitude and longitude data.
     */

    private City getCityByName(String cityName) {
        return cityRepository.findByName(cityName)
                .orElseGet(() -> {
                    City newCity = createCityFromGeoCodingResponse(cityName);
                    return cityRepository.findByName(cityName).orElse(newCity);
                });
    }


    /**
     * Call external API to create a City entity with geocoding data based on city name.
     *
     * @param cityName Name of the city to get geocoding for.
     * @return City entity containing name, country, state, latitude and longitude data.
     */

    public City createCityFromGeoCodingResponse(String cityName) {
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
     * @param city      City entity.
     * @param tzid      Time Zone ID.
     * @param date      Date of the requested sunrise/sunset data.
     * @param formatted Date formatting options.
     * @return SunriseSunset entity.
     */

    private SunriseSunset createSunriseSunsetForCity(City city, String tzid, LocalDate date, int formatted) {
        String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        SunriseSunsetResponseDTO response = sunriseSunsetApiClient.getSunriseSunsetByCoordinates(city.getLat(), city.getLon(), dateString, tzid, formatted);
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
     * @param city          City entity.
     * @param sunriseSunset SunriseSunset entity.
     * @return CityResponseDTO, containing the cities name, country and sunrise/sunset data.
     */

    private CityDetailsDTO convertToCityDetailsDTO(City city, SunriseSunset sunriseSunset) {
        CityDetailsDTO cityDetailsDTO = new CityDetailsDTO();
        cityDetailsDTO.setName(city.getName());
        cityDetailsDTO.setCountry(city.getCountry());

        SunriseSunsetDTO sunriseSunsetDTO = convertToSunriseSunsetDTO(sunriseSunset);
        cityDetailsDTO.setSunriseSunset(sunriseSunsetDTO);
        return cityDetailsDTO;
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

    private CityDTO convertToCityDTO(City city) {
        CityDTO cityDTO = new CityDTO();
        cityDTO.setName(city.getName());
        cityDTO.setCountry(city.getCountry());

        return cityDTO;
    }

    public CityDTO updateCity(CityDTO city) {
        Optional<City> existingCity = cityRepository.findByName(city.getName());

        if (existingCity.isPresent()) {
            City cityToUpdate = existingCity.get();
            cityToUpdate.setName(city.getName());
            cityToUpdate.setCountry(city.getCountry());

            cityRepository.save(cityToUpdate);

            return convertToCityDTO(cityToUpdate);
        }

        throw new NoSuchElementException("City not found");
    }

    /**
     * Get sunrise/sunset data on a given date for a city. Searches database for existing data and calls external API if no data was found.
     *
     * @param cityName  Name of the city.
     * @param date      Date of sunrise/sunset.
     * @param tzid      Time Zone ID.
     * @param formatted Date formatting options
     * @return CityResponseDTO, containing the cities name, country and sunrise/sunset data.
     */

    public CityDetailsDTO getSolarTimes(String cityName, LocalDate date, String tzid, int formatted) {
        City city = getCityByName(cityName);

        Optional<SunriseSunset> sunriseSunset = sunriseSunsetRepository.findByCityAndDate(city, date);

        if (sunriseSunset.isPresent()) {
            return convertToCityDetailsDTO(city, sunriseSunset.get());
        } else {
            SunriseSunset newSunriseSunset = createSunriseSunsetForCity(city, tzid, date, formatted);
            return convertToCityDetailsDTO(city, newSunriseSunset);
        }
    }

    public void deleteCity(CityDTO city) {
        City cityToDelete = cityRepository.findByName(city.getName())
                .orElseThrow(() -> new NoSuchElementException("City not found"));

        cityRepository.delete(cityToDelete);
    }

    public SunriseSunsetDTO createSunriseSunset(SunriseSunsetDTO sunriseSunsetDTO) {
        SunriseSunset sunriseSunset = new SunriseSunset();
        sunriseSunset.setSunrise(sunriseSunsetDTO.sunrise());
        sunriseSunset.setSunset(sunriseSunsetDTO.sunset());

        sunriseSunsetRepository.save(sunriseSunset);

        return convertToSunriseSunsetDTO(sunriseSunset);
    }

    public SunriseSunsetDTO updateSunriseSunset(Long id, SunriseSunsetDTO updatedSunriseSunsetDTO) {
        SunriseSunset sunriseSunset = sunriseSunsetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("SunriseSunset not found"));

        sunriseSunset.setSunrise(updatedSunriseSunsetDTO.sunrise());
        sunriseSunset.setSunset(updatedSunriseSunsetDTO.sunset());
        sunriseSunsetRepository.save(sunriseSunset);

        return convertToSunriseSunsetDTO(sunriseSunset);

    }

    public void deleteSunriseSunset(Long id) {
        SunriseSunset sunriseSunsetToDelete = sunriseSunsetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("SunriseSunset with id: " + id + "not found"));

        sunriseSunsetRepository.delete(sunriseSunsetToDelete);
    }
}
