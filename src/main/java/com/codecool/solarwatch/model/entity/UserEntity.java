package com.codecool.solarwatch.model.entity;

import java.util.Set;

public record UserEntity(String username, String password, Set<Role> roles) {
}
