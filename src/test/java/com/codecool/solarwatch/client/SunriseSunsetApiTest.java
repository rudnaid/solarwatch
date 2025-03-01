//package com.codecool.solarwatch.client;
//
//import com.codecool.solarwatch.model.dto.SunriseSunsetDTO;
//import com.codecool.solarwatch.model.dto.SunriseSunsetResponseDTO;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.ZonedDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class SunriseSunsetApiTest {
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @InjectMocks
//    private SunriseSunsetApiClient sunriseSunsetApiClient;
//
//    @Test
//    void getSunriseSunsetByCoordinates_GivenValidGeoCoordinates_ReturnsSunriseSunset() {
//        double lat = 52.33;
//        double lng = 23.11;
//        String tzid = "test";
//        String date = "testDate";
//        int formatted = 0;
//
//        ZonedDateTime mockSunrise = ZonedDateTime.parse("2015-05-21T05:05:35+00:00");
//        ZonedDateTime mockSunset = ZonedDateTime.parse("2015-05-21T19:22:59+00:00");
//
//        SunriseSunsetDTO mockSunriseSunsetDTO = new SunriseSunsetDTO(mockSunrise, mockSunset);
//        SunriseSunsetResponseDTO mockResponse = new SunriseSunsetResponseDTO(mockSunriseSunsetDTO);
//
//        when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(lat, lng, date, tzid, formatted)).thenReturn(mockResponse);
//
//        SunriseSunsetResponseDTO response = sunriseSunsetApiClient.getSunriseSunsetByCoordinates(lat, lng, date, tzid, formatted);
//
//        assertNotNull(response);
//        assertEquals(mockSunrise, response.results().sunrise());
//        assertEquals(mockSunset, response.results().sunset());
//    }
//
//    @Test
//    void getSunriseSunsetByCoordinates_GivenInvalidGeoCoordinates_ThrowsException() {
//
//    }
//}
