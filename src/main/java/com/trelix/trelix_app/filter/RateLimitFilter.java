package com.trelix.trelix_app.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trelix.trelix_app.dto.response.ErrorResponse;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.security.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_SECONDS = 60;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            filterChain.doFilter(request, response);
            return;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            UUID userId = userDetails.getId();
            String key = "ratelimit:" + userId.toString();

            try {
                Long count = redisTemplate.opsForValue().increment(key);

                if (count != null && count == 1) {
                    redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
                }

                if (count != null && count > MAX_REQUESTS) {
                    sendRateLimitResponse(response, request.getRequestURI());
                    return;
                }
            } catch (Exception e) {
                log.warn("Rate limiting unavailable (Redis issue): {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendRateLimitResponse(HttpServletResponse response, String path) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now().toString(),
                path,
                ErrorCode.RATE_LIMIT_EXCEEDED.name(),
                "Rate limit exceeded. Please try again later.");

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
