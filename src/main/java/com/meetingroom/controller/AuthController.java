package com.meetingroom.controller;

import com.meetingroom.dto.AuthDTO;
import com.meetingroom.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticação", description = "Endpoints de autenticação e geração de token JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Realizar login",
        description = "Autentica o usuário e retorna um token JWT. " +
                      "Use: admin/admin123 ou user/user123"
    )
    public ResponseEntity<AuthDTO.LoginResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        log.info("POST /api/auth/login - usuário: {}", request.getUsername());
        AuthDTO.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
