package com.meetingroom.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API de Gerenciamento de Salas de Reunião",
        version = "1.0.0",
        description = "API RESTful para gerenciamento de salas de reunião e reservas com arquitetura SOA, " +
                      "autenticação JWT, banco H2 e documentação Swagger.",
        contact = @Contact(
            name = "Desenvolvedor",
            email = "aluno@faculdade.edu.br"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Servidor Local")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "Token JWT de autenticação. Formato: Bearer {token}",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}
