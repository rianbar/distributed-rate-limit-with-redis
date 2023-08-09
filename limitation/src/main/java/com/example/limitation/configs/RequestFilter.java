package com.example.limitation.configs;

import com.example.limitation.domain.services.InMemoryUserService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    RateLimiterConfig rateLimiter;

    @Autowired
    InMemoryUserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(request.getRequestURI().startsWith("/v1")) {
            if(authorization != null && authorization.toLowerCase().startsWith("basic")) {
                // Authorization: Basic base64credentials
                String base64Credentials = authorization.substring("Basic".length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                // credentials = username:password
                final String[] values = credentials.split(":", 2);
                String username = values[0];


                UserDetails user = userService.loadUserByUsername(username);
                String authority = String.valueOf(user.getAuthorities().iterator().next());

                Bucket bucket = rateLimiter.resolveBucket(authority);
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
