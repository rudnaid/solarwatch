package com.codecool.solarwatch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SolarTimes(
        ZonedDateTime sunrise,
        ZonedDateTime sunset,
        ZonedDateTime solarNoon,
        ZonedDateTime civilTwilightBegin,
        ZonedDateTime civilTwilightEnd,
        ZonedDateTime nauticalTwilightBegin,
        ZonedDateTime nauticalTwilightEnd,
        ZonedDateTime astronomicalTwilightBegin,
        ZonedDateTime astronomicalTwilightEnd
) { }