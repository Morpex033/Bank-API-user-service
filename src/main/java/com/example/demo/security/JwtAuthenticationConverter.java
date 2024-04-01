package com.example.demo.security;

import com.example.demo.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {
    private final TokenService tokenService;

    @Override
    public Authentication convert(HttpServletRequest request) {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            var token = authorization.replace("Bearer ", "");
            var accessToken = this.tokenService.accessTokenStringDeserializer(token);
            if (accessToken != null) {
                return new PreAuthenticatedAuthenticationToken(accessToken, token);
            }

            var refreshToken = this.tokenService.refreshTokenStringDeserializer(token);
            if (refreshToken != null) {
                return new PreAuthenticatedAuthenticationToken(refreshToken, token);
            }
        }

        return null;
    }
}
