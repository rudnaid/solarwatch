package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.dto.CityDTO;
import com.codecool.solarwatch.model.dto.CityDetailsDTO;
import com.codecool.solarwatch.model.dto.SunriseSunsetDTO;
import com.codecool.solarwatch.service.SolarWatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solarwatch")
public class SolarWatchController {

    private final SolarWatchService solarWatchService;

    @Autowired
    public SolarWatchController(SolarWatchService solarWatchService) {
        this.solarWatchService = solarWatchService;
    }

    /**
     * Provide sunrise/sunset times for a given city.
     *
     * @param city      Name of the city.
     * @param date      Date of requested sunrise/sunset data. When omitted, default is current date.
     * @param tzid      Time Zone ID.
     * @param formatted 0: ZonedDateTime format, 1: YYYY-MM-DD-hhss format.
     * @return CityDetailsDTO object.
     */
    @GetMapping("/times")
    @PreAuthorize("hasRole('USER')")
    public CityDetailsDTO getSolarTimes(
            @RequestParam String city,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "UTC") String tzid,
            @RequestParam(defaultValue = "0") int formatted) {

        return solarWatchService.getSolarTimes(city, date, tzid, formatted);
    }

    @PostMapping("/cities")
    @PreAuthorize("hasRole('ADMIN')")
    public void addCity(@RequestBody CityDTO city) {
        solarWatchService.createCityFromGeoCodingResponse(city.getName());
    }

    @PutMapping("/cities")
    @PreAuthorize("hasRole('ADMIN')")
    public CityDTO updateCity(@RequestBody CityDTO city) {
        return solarWatchService.updateCity(city);
    }

    @DeleteMapping("/cities")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCity(@RequestBody CityDTO city) {
        solarWatchService.deleteCity(city);
    }

    @PostMapping("/sunrisesunset")
    @PreAuthorize("hasRole('ADMIN')")
    public SunriseSunsetDTO createSunriseSunset(@RequestBody SunriseSunsetDTO sunriseSunset) {
        return solarWatchService.createSunriseSunset(sunriseSunset);
    }


    @PutMapping("/sunrisesunset/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SunriseSunsetDTO updateSunriseSunset(@PathVariable Long id, @RequestBody SunriseSunsetDTO updatedSunriseSunsetDTO) {
        return solarWatchService.updateSunriseSunset(id, updatedSunriseSunsetDTO);
    }

    @DeleteMapping("/sunrisesunset/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSunriseSunset(@PathVariable Long id) {
        solarWatchService.deleteSunriseSunset(id);
    }
}
