package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.service.SolarWatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/solarwatch")
public class SolarTimesController {
    private final SolarWatchService solarWatchService;

    @Autowired
    public SolarTimesController(SolarWatchService solarWatchService) {
        this.solarWatchService = solarWatchService;
    }

    @GetMapping("/times")
    public ResponseEntity<?> getSolarTimes(
            @RequestParam String city,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "Europe") String tzid,
            @RequestParam(defaultValue = "0") int formatted) {

        if (date == null) {
            date = LocalDate.now().toString();
        }

        var result = solarWatchService.getSolarTimes(city, date, tzid, formatted);

        return ResponseEntity.ok(result);
    }

}
