package com.mealtracker.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationHandler authenticationHandler;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    public JwtAuthenticationFilter(JwtAuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            log.debug("Processing JWT authentication for request: {}", request.getRequestURI());
            var authOptional = authenticationHandler.authenticate(request);
            if (authOptional.isEmpty()) {
                log.debug("No JWT token found in request");
                filterChain.doFilter(request, response);
                return;
            }
            var authentication = authOptional.get();
            log.debug("JWT authentication successful for user: {}", authentication.getName());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            securityContextHolderStrategy.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            log.debug("Authentication set in SecurityContext: {}", securityContextHolderStrategy.getContext().getAuthentication());
        } catch (JwtValidationException ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            // Let the request continue without authentication - security filter will handle it
        }
        filterChain.doFilter(request, response);
    }
}
