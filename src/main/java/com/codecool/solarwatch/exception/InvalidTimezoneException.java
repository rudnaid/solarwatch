package com.codecool.solarwatch.exception;

public class InvalidTimezoneException extends RuntimeException {
    public InvalidTimezoneException() {
        super("Invalid timezone provided");
    }
}
