package com.lolmaxlevel.oneclone_backend.service.auth;

import com.lolmaxlevel.oneclone_backend.dto.JwtRequest;
import com.lolmaxlevel.oneclone_backend.dto.JwtResponse;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;

public interface AuthService {
    JwtResponse login(@NonNull JwtRequest request) throws AuthException;

    JwtResponse refreshAccessToken(@NonNull String refreshToken) throws AuthException;
}
