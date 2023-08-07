package com.example.limitation.configs;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    RateLimiterConfig rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if(request.getRequestURI().startsWith("/v1")) {
            String tenantId = request.getHeader("X-Tenant");
            if(!tenantId.isEmpty()) {
                UUID parseUUID = UUID.fromString(tenantId);
                Bucket bucket = rateLimiter.resolveBucket(parseUUID);
                if (bucket.tryConsume(1)) {
                    filterChain.doFilter(request, response);
                } else {
                    sendErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS.value());
                }
            } else {
                sendErrorResponse(response, HttpStatus.FORBIDDEN.value());
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int value) {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setStatus(value);
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
