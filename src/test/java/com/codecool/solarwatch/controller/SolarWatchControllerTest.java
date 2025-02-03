package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.SolarTimes;
import com.codecool.solarwatch.model.SolarTimesResponse;
import com.codecool.solarwatch.service.SolarWatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolarTimesController.class)
public class SolarWatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SolarWatchService solarWatchService;

    @Test
    void getSolarTimes_ValidRequest_ReturnsOk() throws Exception {
        String city = "TestCity";
        String date = "2025-01-30";
        String tzid = "Europe";
        int formatted = 0;

        ZonedDateTime mockSunrise = ZonedDateTime.parse("2015-05-21T05:05:35+00:00");
        ZonedDateTime mockSunset = ZonedDateTime.parse("2015-05-21T19:22:59+00:00");

        SolarTimes mockSolarTimes = new SolarTimes(mockSunrise, mockSunset);
        SolarTimesResponse mockResponse = new SolarTimesResponse(mockSolarTimes);
        when(solarWatchService.getSolarTimes(city, date, tzid, formatted)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/solarwatch/times")
                        .param("city", city)
                        .param("date", date)
                        .param("tzid", tzid)
                        .param("formatted", String.valueOf(formatted)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").exists());
    }

    @Test
    void getSolarTimes_InvalidRequest_ReturnsBadRequest() throws Exception {
        String city = "";
        String date = "2025-01-30";
        String tzid = "Europe";
        int formatted = 0;

        mockMvc.perform(get("/api/solarwatch/times")
                        .param("city", city)
                        .param("date", date)
                        .param("tzid", tzid)
                        .param("formatted", String.valueOf(formatted)))
                .andExpect(status().isBadRequest());
    }
}