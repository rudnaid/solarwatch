package com.codecool.solarwatch.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException() { super("Invalid latitude or longitude parameters provided."); }
}
