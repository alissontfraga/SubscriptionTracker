package com.alissontfraga.subscriptiontracker.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alissontfraga.subscriptiontracker.dto.auth.UserResponse;
import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return new UserResponse(user.getId(), user.getUsername());
    }
}
