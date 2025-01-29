package com.codecool.solarwatch.exception;

public class UnknownApiErrorException extends RuntimeException {
    public UnknownApiErrorException() {super("Unknown API error occurred, please try again"); }
}
