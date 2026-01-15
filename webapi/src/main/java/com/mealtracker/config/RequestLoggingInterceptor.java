package com.mealtracker.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Logs incoming HTTP requests and responses.
 * Logs include method, URI, status code, and execution time.
 */
@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        log.info("Incoming request: {} {} from {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {

        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long executionTime = startTime != null ? System.currentTimeMillis() - startTime : 0;

        if (ex != null) {
            log.error("Request failed: {} {} - Status: {} - Time: {}ms - Error: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    executionTime,
                    ex.getMessage());
        } else {
            log.info("Request completed: {} {} - Status: {} - Time: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    executionTime);
        }
    }
}
