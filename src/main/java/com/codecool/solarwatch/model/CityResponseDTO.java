package com.codecool.solarwatch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityResponseDTO {
    private String name;
    private String country;
    private SunriseSunsetDTO sunriseSunset;
}
