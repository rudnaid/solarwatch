package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.model.payload.UserPromotionRequest;
import com.codecool.solarwatch.model.payload.UserRequest;
import com.codecool.solarwatch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void createUser(@RequestBody UserRequest registerRequest) {
        userService.createUser(registerRequest);
    }

    @PostMapping("/login")
    public JwtResponse authenticateUser(@RequestBody UserRequest loginRequest) {
        return userService.authenticateUser(loginRequest);
    }

    @PutMapping("/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public void promoteToAdmin(@RequestBody UserPromotionRequest promotionRequest) {
        userService.promoteToAdmin(promotionRequest.username());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public String me() {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return "Hello " + user.getUsername();
    }
}
