package com.codecool.solarwatch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
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

}
