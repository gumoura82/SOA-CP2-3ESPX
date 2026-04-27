package com.meetingroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Credenciais de login")
    public static class LoginRequest {

        @NotBlank(message = "Username é obrigatório")
        @Schema(description = "Nome de usuário", example = "admin")
        private String username;

        @NotBlank(message = "Senha é obrigatória")
        @Schema(description = "Senha do usuário", example = "admin123")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Resposta com token JWT")
    public static class LoginResponse {

        @Schema(description = "Token JWT para autenticação")
        private String token;

        @Schema(description = "Tipo do token", example = "Bearer")
        private String type;

        @Schema(description = "Nome de usuário autenticado")
        private String username;

        @Schema(description = "Papel do usuário")
        private String role;
    }
}
