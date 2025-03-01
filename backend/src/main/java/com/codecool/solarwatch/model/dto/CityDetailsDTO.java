package com.codecool.solarwatch.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityDetailsDTO {
    private String name;
    private String country;
    private SunriseSunsetDTO sunriseSunset;
}
