package com.codecool.solarwatch.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    private String id;
    private String name;
    private String country;
    private String state;
    private double lat;
    private double lon;

    @OneToMany(mappedBy = "city")
    private List<SunriseSunset> sunriseSunsets;


    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public List<SunriseSunset> getSunriseSunsets() {
        return sunriseSunsets;
    }
}
