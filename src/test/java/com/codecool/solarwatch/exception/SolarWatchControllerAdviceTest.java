package com.codecool.solarwatch.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SolarWatchControllerAdviceTest {

    private final SolarWatchControllerAdvice solarWatchControllerAdvice = new SolarWatchControllerAdvice();

    @Test
    void handleInvalidCityException_ShouldReturnBadRequest() {
    }

}
