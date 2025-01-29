package com.codecool.solarwatch.exception;

public class InvalidCityException extends RuntimeException {
    public InvalidCityException() {
        super("Invalid city name provided");
    }
}
