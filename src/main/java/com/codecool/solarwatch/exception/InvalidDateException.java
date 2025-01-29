package com.codecool.solarwatch.exception;

public class InvalidDateException extends RuntimeException {
    public InvalidDateException() {
        super("Invalid date provided");
    }
}
