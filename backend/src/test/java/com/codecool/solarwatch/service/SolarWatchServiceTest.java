package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.exception.GeoCodingApiResponseException;
import com.codecool.solarwatch.model.dto.*;
import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SunriseSunset;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunriseSunsetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SolarWatchServiceTest {

    @Mock
    private GeoCodingApiClient geoCodingApiClient;

    @Mock
    private SunriseSunsetApiClient sunriseSunsetApiClient;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private SunriseSunsetRepository sunriseSunsetRepository;

    @InjectMocks
    private SolarWatchService solarWatchService;

    @Test
    void createCityFromGeoCodingResponse_shouldThrow_whenApiReturnsEmpty() {
        when(geoCodingApiClient.getGeoCoordinatesForCity("NonExistingCity")).thenReturn(new GeoCodingResponseDTO[0]);

        assertThrows(GeoCodingApiResponseException.class, () -> {
            solarWatchService.createCityFromGeoCodingResponse("NonExistingCity");
        });
    }

    @Test
    void getSolarTimes_shouldFetchFromApi_whenNotInDb() {
        ZonedDateTime sunrise = ZonedDateTime.parse("2025-04-22T06:00:00+01:00[Europe/Paris]");
        ZonedDateTime sunset = ZonedDateTime.parse("2025-04-22T20:00:00+01:00[Europe/Paris]");

        City city = new City();
        city.setName("Paris");
        city.setCountry("France");
        city.setState(null);
        city.setLat(48.85);
        city.setLon(2.35);

        SunriseSunset sunriseSunset = new SunriseSunset();
        sunriseSunset.setSunrise(sunrise);
        sunriseSunset.setSunset(sunset);

        when(cityRepository.findByName("Paris")).thenReturn(Optional.empty());

        when(geoCodingApiClient.getGeoCoordinatesForCity("Paris"))
                .thenReturn(new GeoCodingResponseDTO[]{new GeoCodingResponseDTO("Paris", 48.85, 2.35, "FR", null)});

        when(cityRepository.save(any(City.class))).thenReturn(city);

        when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(anyDouble(), anyDouble(), any(), any(), anyInt()))
                .thenReturn(new SunriseSunsetResponseDTO(new SunriseSunsetDTO(sunrise, sunset)));

        when(sunriseSunsetRepository.save(any())).thenReturn(sunriseSunset);

        when(sunriseSunsetRepository.findByCityAndDate(any(), any())).thenReturn(Optional.empty());

        CityDetailsDTO result = solarWatchService.getSolarTimes("Paris", "2025-04-22", "Europe/Paris", 0);
        assertEquals(ZonedDateTime.parse("2025-04-22T06:00:00+01:00[Europe/Paris]"), result.getSunriseSunset().sunrise());
    }

    @Test
    void updateCity_shouldThrow_whenCityNotFound() {
        CityDTO dto = new CityDTO();
        dto.setName("NonExistingCity");
        dto.setCountry("NonExistingCountry");

        when(cityRepository.findByName("NonExistingCity")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> solarWatchService.updateCity(dto));
    }

    @Test
    void deleteCity_shouldThrow_whenCityNotFound() {
        when(cityRepository.findByName("Atlantis")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            CityDTO dto = new CityDTO();
            dto.setName("Atlantis");
            dto.setCountry("ZZ");

            solarWatchService.deleteCity(dto);

        });
    }

    @Test
    void createSunriseSunset_shouldSaveAndReturnDTO() {
        ZonedDateTime sunrise = ZonedDateTime.parse("2023-04-22T05:00:00+00:00[UTC]");
        ZonedDateTime sunset = ZonedDateTime.parse("2023-04-22T20:00:00+00:00[UTC]");

        SunriseSunsetDTO inputDTO = new SunriseSunsetDTO(sunrise, sunset);
        SunriseSunset savedEntity = new SunriseSunset();
        savedEntity.setSunrise(sunrise);
        savedEntity.setSunset(sunset);

        when(sunriseSunsetRepository.save(any(SunriseSunset.class))).thenReturn(savedEntity);

        SunriseSunsetDTO result = solarWatchService.createSunriseSunset(inputDTO);

        assertEquals(sunrise, result.sunrise());
        assertEquals(sunset, result.sunset());
    }

    @Test
    void updateSunriseSunset_shouldThrow_whenIdNotFound() {
        when(sunriseSunsetRepository.findById(123L)).thenReturn(Optional.empty());

        SunriseSunsetDTO updateDTO = new SunriseSunsetDTO(
                ZonedDateTime.now(), ZonedDateTime.now()
        );

        assertThrows(NoSuchElementException.class, () ->
                solarWatchService.updateSunriseSunset(123L, updateDTO)
        );
    }

    @Test
    void deleteSunriseSunset_shouldThrow_whenIdNotFound() {
        when(sunriseSunsetRepository.findById(456L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                solarWatchService.deleteSunriseSunset(456L)
        );
    }

    @Test
    void updateCity_shouldUpdateAndReturnCityDTO() {
        City existingCity = new City();
        existingCity.setName("OldName");
        existingCity.setCountry("OldCountry");

        CityDTO updatedDTO = new CityDTO();
        updatedDTO.setName("NewName");
        updatedDTO.setCountry("NewCountry");

        when(cityRepository.findByName("NewName")).thenReturn(Optional.of(existingCity));
        when(cityRepository.save(any(City.class))).thenReturn(existingCity);

        CityDTO result = solarWatchService.updateCity(updatedDTO);

        assertEquals("NewName", result.getName());
        assertEquals("NewCountry", result.getCountry());
    }

    @Test
    void updateSunriseSunset_shouldUpdateAndReturnDTO() {
        ZonedDateTime sunrise = ZonedDateTime.parse("2025-04-22T05:00:00+00:00[UTC]");
        ZonedDateTime sunset = ZonedDateTime.parse("2025-04-22T21:00:00+00:00[UTC]");

        SunriseSunset existing = new SunriseSunset();
        existing.setSunrise(ZonedDateTime.parse("2025-04-22T04:00:00+00:00[UTC]"));
        existing.setSunset(ZonedDateTime.parse("2025-04-22T20:00:00+00:00[UTC]"));

        SunriseSunsetDTO updatedDTO = new SunriseSunsetDTO(sunrise, sunset);

        when(sunriseSunsetRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(sunriseSunsetRepository.save(existing)).thenReturn(existing);

        SunriseSunsetDTO result = solarWatchService.updateSunriseSunset(1L, updatedDTO);

        assertEquals(sunrise, result.sunrise());
        assertEquals(sunset, result.sunset());
    }

    @Test
    void deleteSunriseSunset_shouldDeleteSuccessfully() {
        SunriseSunset sunriseSunset = new SunriseSunset();

        when(sunriseSunsetRepository.findById(999L)).thenReturn(Optional.of(sunriseSunset));

        solarWatchService.deleteSunriseSunset(999L);

        Mockito.verify(sunriseSunsetRepository).delete(sunriseSunset);
    }
}
