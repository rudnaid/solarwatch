package com.codecool.solarwatch.model.payload;

public record UserRequest(
        String username,
        String password
) {
}
