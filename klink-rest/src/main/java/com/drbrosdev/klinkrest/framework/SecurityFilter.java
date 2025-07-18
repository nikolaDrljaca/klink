package com.drbrosdev.klinkrest.framework;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Log4j2
public class SecurityFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-Api-Key";

    @Value("${app.security.api-key:}")
    private String loadedApiKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // allow requests to health
        if (request.getRequestURI().startsWith("/api/health")) {
            filterChain.doFilter(request, response);
            return;
        }
        // ignore requests that are not hitting /api
        if (!request.getRequestURI().startsWith("/api")) {
            filterChain.doFilter(request, response);
            return;
        }
        // extract api key
        var apiKey = request.getHeader(API_KEY_HEADER);
        // keys match - let the request pass
        if (Objects.equals(apiKey, loadedApiKey)) {
            filterChain.doFilter(request, response);
            return;
        }
        // keys do not match - reject request
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
