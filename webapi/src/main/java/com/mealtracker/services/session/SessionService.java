package com.mealtracker.services.session;

import com.mealtracker.security.jwt.JwtTokenProvider;
import com.mealtracker.services.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public SessionService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    public AccessToken generateToken(SessionInput sessionInput) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                sessionInput.email(),
                sessionInput.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var token = jwtTokenProvider.generateToken(authentication);
        return AccessToken.jwt(token);
    }
}
