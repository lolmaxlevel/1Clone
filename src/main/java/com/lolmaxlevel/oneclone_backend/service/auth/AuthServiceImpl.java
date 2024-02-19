package com.lolmaxlevel.oneclone_backend.service.auth;


import com.lolmaxlevel.oneclone_backend.dto.JwtRequest;
import com.lolmaxlevel.oneclone_backend.dto.JwtResponse;
import com.lolmaxlevel.oneclone_backend.security.UserPasswordUtil;
import com.lolmaxlevel.oneclone_backend.security.jwt.JwtProvider;
import com.lolmaxlevel.oneclone_backend.service.user.UserService;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;


    @Override
    public JwtResponse login(@NonNull JwtRequest request) throws AuthException {
        final var user = userService.getUser(request.username())
                .orElseThrow(() -> new AuthException("User not found"));
        if (!UserPasswordUtil.checkPassword(request.password(), user.getPassword())) {
            throw new AuthException("Wrong password");
        }
        final var accessToken = jwtProvider.generateAccessToken(user);
        final var refreshToken = jwtProvider.generateRefreshToken(user);
        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public JwtResponse refreshAccessToken(@NonNull String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final var claims = jwtProvider.getRefreshClaims(refreshToken);
            final var login = claims.getSubject();
            final var user = userService.getUser(login)
                    .orElseThrow(() -> new AuthException("User not found"));
            final var accessToken = jwtProvider.generateAccessToken(user);
            final var newRefreshToken = jwtProvider.generateRefreshToken(user);
            return new JwtResponse(accessToken, newRefreshToken);
        }
        throw new AuthException("Refresh token not found");
    }
}
