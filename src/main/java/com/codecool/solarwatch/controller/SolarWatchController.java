package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.service.SolarWatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
     * @return CityDTO object wrapped in a ResponseEntity.
     */

    @GetMapping("/times")
    public ResponseEntity<?> getSolarTimes(
            @RequestParam String city,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "UTC") String tzid,
            @RequestParam(defaultValue = "0") int formatted) {

        LocalDate requestDate;

        if (date == null) {
            requestDate = LocalDate.now();
        } else {
            try {
                requestDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        var result = solarWatchService.getSolarTimes(city, requestDate, tzid, formatted);

        return ResponseEntity.ok(result);
    }

}
