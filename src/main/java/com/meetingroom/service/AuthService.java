package com.meetingroom.service;

import com.meetingroom.dto.AuthDTO;
import com.meetingroom.entity.User;
import com.meetingroom.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        log.info("Tentativa de login para usuário: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = (User) userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(user);

        log.info("Login bem-sucedido para usuário: {}", request.getUsername());

        return AuthDTO.LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
