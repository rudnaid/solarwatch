package com.codecool.solarwatch.model;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
public class SunriseSunset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    private String id;
    private ZonedDateTime sunrise;
    private ZonedDateTime sunset;

    @ManyToOne
    private City city;

    public ZonedDateTime getSunrise() {
        return sunrise;
    }

    public ZonedDateTime getSunset() {
        return sunset;
    }

    public City getCity() {
        return city;
    }
}
