package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.exception.GeoCodingApiResponseException;
import com.codecool.solarwatch.model.dto.*;
import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SunriseSunset;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunriseSunsetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * Get sunrise/sunset data on a given date for a city. Searches database for existing data and calls external API if no data was found.
     *
     * @param cityName  Name of the city.
     * @param date      Date of sunrise/sunset.
     * @param tzid      Time Zone ID.
     * @param formatted Date formatting options.
     * @return CityDetailsDTO, containing the city's name, country, and sunrise/sunset data.
     */
    public CityDetailsDTO getSolarTimes(String cityName, String date, String tzid, int formatted) {
        City city = getCityByName(cityName);
        LocalDate requestDate;

        if (date == null) {
            requestDate = LocalDate.now();
        } else {
            requestDate = LocalDate.parse(date);
        }

        Optional<SunriseSunset> sunriseSunset = sunriseSunsetRepository.findByCityAndDate(city, requestDate);

        if (sunriseSunset.isPresent()) {
            return convertToCityDetailsDTO(city, sunriseSunset.get());
        } else {
            SunriseSunset newSunriseSunset = createSunriseSunsetForCity(city, tzid, requestDate, formatted);
            return convertToCityDetailsDTO(city, newSunriseSunset);
        }
    }

    /**
     * Call external API to create a City entity with geocoding data based on city name.
     *
     * @param cityName Name of the city sent to the external API.
     * @return City entity containing name, country, state, latitude and longitude data.
     * @throws GeoCodingApiResponseException in case of external API response error.
     * @throws IllegalStateException         in case of error mapping the external API response to City entity.
     */
    public City createCityFromGeoCodingResponse(String cityName) {
        ObjectMapper objectMapper = new ObjectMapper();

        GeoCodingResponseDTO[] geoCodingResponse = geoCodingApiClient.getGeoCoordinatesForCity(cityName);

        if (geoCodingResponse == null || geoCodingResponse.length == 0) {
            throw new GeoCodingApiResponseException("Failed to get geocoding data for: " + cityName);
        }

        try {
            City city = objectMapper.convertValue(geoCodingResponse[0], City.class);

            return cityRepository.save(city);

        } catch (Exception e) {
            throw new IllegalStateException("Error mapping GeoCodingResponse to City", e);
        }
    }

    /**
     * Update an existing city with new data.
     *
     * @param city CityDTO containing updated city data.
     * @return Updated CityDTO.
     * @throws NoSuchElementException if the city is not found.
     */
    public CityDTO updateCity(CityDTO city) {
        Optional<City> existingCity = cityRepository.findByName(city.name());

        if (existingCity.isPresent()) {
            City cityToUpdate = existingCity.get();
            cityToUpdate.setName(city.name());
            cityToUpdate.setCountry(city.country());

            cityRepository.save(cityToUpdate);

            return convertToCityDTO(cityToUpdate);
        }

        throw new NoSuchElementException("City: " + city.name() + " not found in database");
    }

    /**
     * Delete an existing city from the database.
     *
     * @param city CityDTO to be deleted.
     * @throws NoSuchElementException if the city is not found.
     */
    public void deleteCity(CityDTO city) {
        City cityToDelete = cityRepository.findByName(city.name())
                .orElseThrow(() -> new NoSuchElementException("City: " + city.name() + " not found in database"));

        cityRepository.delete(cityToDelete);
    }

    /**
     * Create a new SunriseSunset entity and save it to the database.
     *
     * @param sunriseSunsetDTO DTO containing sunrise and sunset data.
     * @return The created SunriseSunsetDTO.
     */
    public SunriseSunsetDTO createSunriseSunset(SunriseSunsetDTO sunriseSunsetDTO) {
        SunriseSunset sunriseSunset = new SunriseSunset();
        sunriseSunset.setSunrise(sunriseSunsetDTO.sunrise());
        sunriseSunset.setSunset(sunriseSunsetDTO.sunset());

        sunriseSunsetRepository.save(sunriseSunset);

        return convertToSunriseSunsetDTO(sunriseSunset);
    }

    /**
     * Update an existing SunriseSunset entity with new data.
     *
     * @param id                      ID of the SunriseSunset entity to update.
     * @param updatedSunriseSunsetDTO Updated SunriseSunsetDTO containing new data.
     * @return Updated SunriseSunsetDTO.
     */
    public SunriseSunsetDTO updateSunriseSunset(Long id, SunriseSunsetDTO updatedSunriseSunsetDTO) {
        SunriseSunset sunriseSunset = findSunriseSunsetById(id);

        sunriseSunset.setSunrise(updatedSunriseSunsetDTO.sunrise());
        sunriseSunset.setSunset(updatedSunriseSunsetDTO.sunset());
        sunriseSunsetRepository.save(sunriseSunset);

        return convertToSunriseSunsetDTO(sunriseSunset);

    }

    /**
     * Delete an existing SunriseSunset entity from the database.
     *
     * @param id ID of the SunriseSunset entity to delete.
     */
    public void deleteSunriseSunset(Long id) {
        SunriseSunset sunriseSunset = findSunriseSunsetById(id);

        sunriseSunsetRepository.delete(sunriseSunset);
    }

    /**
     * Call external API to get sunrise/sunset data for a city based on its latitude and longitude and save the results to database.
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
     * Convert City entity to a CityDTO, filter out unnecessary data.
     *
     * @param city City entity.
     * @return CityDTO, containing the city's name and country.
     */
    private CityDTO convertToCityDTO(City city) {
        return new CityDTO(
                city.getName(),
                city.getCountry()
        );
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
     * Convert City and SunriseSunset entities to a response DTO.
     *
     * @param city          City entity.
     * @param sunriseSunset SunriseSunset entity.
     * @return CityDetailsDTO, containing the city's name, country, and sunrise/sunset data.
     */
    private CityDetailsDTO convertToCityDetailsDTO(City city, SunriseSunset sunriseSunset) {
        SunriseSunsetDTO sunriseSunsetDTO = convertToSunriseSunsetDTO(sunriseSunset);

        return new CityDetailsDTO(
                city.getName(),
                city.getCountry(),
                sunriseSunsetDTO
        );
    }

    /**
     * Find a SunriseSunset entity by its ID.
     *
     * @param id ID of the SunriseSunset entity.
     * @return SunriseSunset entity.
     * @throws NoSuchElementException if the SunriseSunset entity is not found.
     */
    private SunriseSunset findSunriseSunsetById(Long id) {
        return sunriseSunsetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("SunriseSunset with id: " + id + "not found"));
    }

    /**
     * Get City entity based on the name of the city.
     * Searches the database for an existing city. If no city is found, it calls {@link #createCityFromGeoCodingResponse}
     * to retrieve data from an external API, creates a new City entity and saves it to the database.
     *
     * @param cityName Name of the city to search for.
     * @return City entity containing the name, country, state, latitude, and longitude data.
     */
    private City getCityByName(String cityName) {
        return cityRepository.findByName(cityName)
                .orElseGet(() -> {
                    City newCity = createCityFromGeoCodingResponse(cityName);

                    return cityRepository.save(newCity);
                });
    }
}
