package com.safeyard.safeyard_api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Slf4j
@Component
public class RoleInterceptor implements HandlerInterceptor {

    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/", "/login", "/swagger-ui", "/v3/api-docs", "/webjars",
            "/css", "/js", "/images", "/files"
    );

    private boolean isPublic(String path) {
        if (path.equals("/")) return true;
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private boolean hasRole(HttpSession session, String... roles) {
        if (session == null) return false;
        Object r = session.getAttribute("userRole");
        if (r == null) return false;

        String current = String.valueOf(r);
        for (String role : roles) {
            if (current.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String path = req.getRequestURI();

        if (isPublic(path)) {
            return true;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect("/login?denied");
            return false;
        }

        if (path.startsWith("/admin")) {
            if (!hasRole(session, "ADMIN")) {
                res.sendRedirect("/login?denied");
                return false;
            }
        }

        if (path.startsWith("/dashboard") || path.startsWith("/motos")) {
            if (!hasRole(session, "ADMIN", "FUNCIONARIO")) {
                res.sendRedirect("/login?denied");
                return false;
            }
        }

        if (path.startsWith("/cliente")) {
            if (!hasRole(session, "CLIENTE")) {
                res.sendRedirect("/login?denied");
                return false;
            }
        }

        return true;
    }
}
