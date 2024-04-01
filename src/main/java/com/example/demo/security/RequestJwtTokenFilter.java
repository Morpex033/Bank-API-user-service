package com.example.demo.security;

import com.example.demo.models.Tokens;
import com.example.demo.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpMethod;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Data
public class RequestJwtTokenFilter extends OncePerRequestFilter {
    private final RequestMatcher requestMatcher = new AntPathRequestMatcher("/jwt/tokens", HttpMethod.POST.name());

    private final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    private final TokenService tokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.requestMatcher.matches(request)) {
            if (this.securityContextRepository.containsContext(request)) {
                var context = this.securityContextRepository.loadDeferredContext(request).get();
                if (context != null && !(context.getAuthentication() instanceof PreAuthenticatedAuthenticationToken)) {
                    var refreshToken = this.tokenService.refreshToken(context.getAuthentication());
                    var accessToken = this.tokenService.accessToken(refreshToken);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    this.objectMapper.writeValue(response.getWriter(),
                            new Tokens(this.tokenService.accessTokenStringSerializer(accessToken),
                                    this.tokenService.refreshTokenStringSerializer(refreshToken)));
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
