package com.codecool.solarwatch.model.dto;

public record CityDetailsDTO(
        String name,
        String country,
        SunriseSunsetDTO sunriseSunset
) {
}
