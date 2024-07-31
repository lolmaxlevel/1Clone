package com.lolmaxlevel.oneclone_backend.controller;

import com.lolmaxlevel.oneclone_backend.dto.JwtRequest;
import com.lolmaxlevel.oneclone_backend.dto.JwtResponse;
import com.lolmaxlevel.oneclone_backend.dto.RefreshJwtRequest;
import com.lolmaxlevel.oneclone_backend.model.User;
import com.lolmaxlevel.oneclone_backend.service.auth.AuthService;
import com.lolmaxlevel.oneclone_backend.service.user.UserService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        log.info("Login request: {}", request);
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (AuthException e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshJwtRequest request) {
        log.info("Refresh request: {}", request.getRefreshToken());
        try {
            return ResponseEntity.ok(authService.refreshAccessToken(request.getRefreshToken()));
        } catch (AuthException e) {
            log.error("Refresh error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody JwtRequest request) {
        log.info("Register request: {}", request);
        if (request.username() == null || request.password() == null) {
            return ResponseEntity.badRequest().build();
        }

        final var newUser = new User();
        newUser.setUsername(request.username());
        newUser.setPassword(request.password());

        try {
            final var wasAdded = userService.addUser(newUser);
            if (wasAdded) {
                return ResponseEntity.ok(authService.login(request));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (AuthException e) {
            log.error("Register error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


}