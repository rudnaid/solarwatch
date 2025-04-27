package com.codecool.solarwatch.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SunriseSunsetDTO(
        ZonedDateTime sunrise,
        ZonedDateTime sunset
) {
}
