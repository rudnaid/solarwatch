package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.entity.City;
import com.codecool.solarwatch.model.entity.SunriseSunset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SunriseSunsetRepository extends JpaRepository<SunriseSunset, String> {

    @Query("SELECT s FROM SunriseSunset s WHERE s.city = :city AND FUNCTION('DATE', s.sunrise) = :date")
    Optional<SunriseSunset> findByCityAndDate(@Param("city") City city, @Param("date") LocalDate date);

    Optional<SunriseSunset> findById(long id);
}
