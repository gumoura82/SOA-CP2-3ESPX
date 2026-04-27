package com.meetingroom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura padrão de resposta de erro")
public class ErrorResponseDTO {

    @Schema(description = "Timestamp do erro")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP")
    private int status;

    @Schema(description = "Tipo do erro")
    private String error;

    @Schema(description = "Mensagem do erro")
    private String message;

    @Schema(description = "Caminho da requisição")
    private String path;

    @Schema(description = "Lista de erros de validação")
    private List<String> errors;
}
