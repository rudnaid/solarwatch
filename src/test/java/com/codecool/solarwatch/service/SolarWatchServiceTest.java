//package com.codecool.solarwatch.service;
//
//import com.codecool.solarwatch.client.GeoCodingApiClient;
//import com.codecool.solarwatch.client.SunriseSunsetApiClient;
//import com.codecool.solarwatch.model.dto.GeoCodingResponseDTO;
//import com.codecool.solarwatch.model.dto.SunriseSunsetDTO;
//import com.codecool.solarwatch.model.dto.SunriseSunsetResponseDTO;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.ZonedDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class SolarWatchServiceTest {
//
//    @Mock
//    private GeoCodingApiClient geoCodingApiClient;
//
//    @Mock
//    private SunriseSunsetApiClient sunriseSunsetApiClient;
//
//    @InjectMocks
//    private SolarWatchService solarWatchService;
//
//    //TODO fix test, coordinates is null, needs to be mocked?
//
//    @Test
//    void getSolarTimes_GivenValidCity_ReturnsSolarTimes() {
//        String city = "testCity";
//        String date = "2025-01-30";
//        String tzid = "testTZID";
//        int formatted = 0;
//
//        GeoCodingResponseDTO mockCoordinates = new GeoCodingResponseDTO(53.33, 23.43);
//
//        ZonedDateTime mockSunrise = ZonedDateTime.parse("2015-05-21T05:05:35+00:00");
//        ZonedDateTime mockSunset = ZonedDateTime.parse("2015-05-21T19:22:59+00:00");
//
//        SunriseSunsetDTO mockSunriseSunsetDTO = new SunriseSunsetDTO(mockSunrise, mockSunset);
//        SunriseSunsetResponseDTO mockSunriseSunsetResponseDTO = new SunriseSunsetResponseDTO(mockSunriseSunsetDTO);
//
//        when(geoCodingApiClient.getGeoCoordinatesForCity(city)).thenReturn(mockCoordinates);
//
//        when(solarWatchService.getSolarTimes(city, date, tzid, formatted)).thenReturn(mockSunriseSunsetResponseDTO);
//
//        SunriseSunsetResponseDTO response = solarWatchService.getSolarTimes(city, date, tzid, formatted);
//        SunriseSunsetDTO sunriseSunsetDTO = response.results();
//
//        assertNotNull(response);
//        assertEquals(mockSunriseSunsetDTO, sunriseSunsetDTO);
//    }
//
//    @Test
//    void getSolarTimes_GivenInvalidCity_ThrowsInvalidCityException() {
//        String city = "InvalidCity";
//        String date = "2025-01-30";
//        String tzid = "testTZID";
//        int formatted = 0;
//
//        GeoCodingResponseDTO mockCoordinates = new GeoCodingResponseDTO(53.33, 23.43);
//
//        when(geoCodingApiClient.getGeoCoordinatesForCity(city)).thenReturn(mockCoordinates);
//
//        when(solarWatchService.getSolarTimes(city, date, tzid, formatted)).thenThrow(InvalidCityException.class);
//
//        assertThrows(InvalidCityException.class, () -> solarWatchService.getSolarTimes(city, date, tzid, formatted));
//    }
//}
