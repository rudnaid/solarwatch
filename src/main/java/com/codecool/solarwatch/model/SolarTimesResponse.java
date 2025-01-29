package com.codecool.solarwatch.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record SolarTimes(String city, LocalDate date, LocalTime sunriseTime, LocalTime sunsetTime) {
}
