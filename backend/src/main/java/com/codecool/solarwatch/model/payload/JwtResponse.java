package com.codecool.solarwatch.model.payload;

import java.util.List;

public record JwtResponse(
        String jwtToken,
        String username,
        List<String> roles
) {
}
