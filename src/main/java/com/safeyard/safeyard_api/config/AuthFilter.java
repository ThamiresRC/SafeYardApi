package com.safeyard.safeyard_api.config;

import java.io.IOException;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.safeyard.safeyard_api.repository.UserRepository;
import com.safeyard.safeyard_api.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/",
            "/api/integrations/health",
            "/api/integrations/events",
            "/actuator/health",
            "/h2-console/",
            "/swagger-ui/", "/v3/api-docs/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null && currentAuth.isAuthenticated()) {
            chain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        String token = getToken(request);
        if (token == null || token.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String email = tokenService.getSubject(token);
            if (email != null && !email.isBlank()) {
                userRepository.findByEmail(email).ifPresent(user -> {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            }
        } catch (Exception ex) {

        }

        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authHeader.substring(7).trim();
        }
        return null;
    }

    private boolean isPublic(String uri) {
        if (uri == null || uri.isEmpty()) return true;
        String normalized = uri.endsWith("/") ? uri : uri + "/";
        for (String pub : PUBLIC_PATHS) {
            if (normalized.startsWith(pub)) {
                return true;
            }
        }
        return false;
    }
}
