package com.codecool.solarwatch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
public class SunriseSunset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDate createdAt;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime sunrise;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")

    private ZonedDateTime sunset;

    @ManyToOne
    private City city;

}
