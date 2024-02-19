package com.lolmaxlevel.oneclone_backend.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {
    public static JwtAuth generate(Claims claims) {
        final JwtAuth jwtInfoToken = new JwtAuth();
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

}