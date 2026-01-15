package com.mealtracker.config;

import com.mealtracker.config.properties.JwtProperties;
import com.mealtracker.security.RestAccessDeniedHandler;
import com.mealtracker.security.RestAuthenticationEntryPoint;
import com.mealtracker.security.jwt.JwtAuthenticationFilter;
import com.mealtracker.security.jwt.JwtAuthenticationHandler;
import com.mealtracker.security.jwt.JwtTokenProvider;
import com.mealtracker.security.jwt.JwtTokenValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Bean
    @ConfigurationProperties("app.jwt")
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(JwtProperties jwtProperties) {
        return new JwtTokenProvider(jwtProperties.getSecretKey());
    }

    @Bean
    public JwtTokenValidator jwtTokenValidator(JwtProperties jwtProperties) {
        return new JwtTokenValidator(jwtProperties.getSecretKey());
    }

    @Bean
    public JwtAuthenticationHandler jwtAuthenticationHandler(JwtTokenProvider jwtTokenProvider,
                                                             JwtTokenValidator jwtTokenValidator) {
        return new JwtAuthenticationHandler(jwtTokenProvider, jwtTokenValidator);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtAuthenticationHandler authenticationHandler) {
        return new JwtAuthenticationFilter(authenticationHandler);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        var emptyRoleVoterPrefix = "";
        return new GrantedAuthorityDefaults(emptyRoleVoterPrefix);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(new RestAccessDeniedHandler(exceptionResolver))
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint(exceptionResolver)))
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(request -> request.getMethod().equals("GET") &&
                                request.getRequestURI().matches("\\/v1\\/users\\/?\\?email=.*"))
                        .permitAll()
                        .requestMatchers("/v1/users").permitAll()
                        .requestMatchers("/v1/sessions").permitAll()
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/actuator/info").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return authentication -> authenticationProvider.authenticate(authentication);
    }
}
