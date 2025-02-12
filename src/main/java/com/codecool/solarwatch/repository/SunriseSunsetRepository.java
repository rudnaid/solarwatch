package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SunriseSunset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SunriseSunsetRepository extends JpaRepository<SunriseSunset, String> {

    Optional<SunriseSunset> findSunriseSunsetByCityId(String cityId);
}
