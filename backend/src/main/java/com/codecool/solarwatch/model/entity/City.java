package com.codecool.solarwatch.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
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
