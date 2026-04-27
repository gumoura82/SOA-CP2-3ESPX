package com.meetingroom.dto;

import com.meetingroom.entity.Sala;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SalaDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Dados para criação ou atualização de sala")
    public static class Request {

        @NotBlank(message = "Nome é obrigatório")
        @Schema(description = "Nome da sala", example = "Sala de Reunião Principal")
        private String nome;

        @Min(value = 1, message = "Capacidade mínima é 1")
        @Schema(description = "Capacidade da sala", example = "10")
        private Integer capacidade;

        @NotBlank(message = "Localização é obrigatória")
        @Schema(description = "Localização da sala", example = "Andar 3 - Ala Norte")
        private String localizacao;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Dados de retorno de uma sala")
    public static class Response {
        @Schema(description = "ID da sala")
        private Long id;
        @Schema(description = "Nome da sala")
        private String nome;
        @Schema(description = "Capacidade da sala")
        private Integer capacidade;
        @Schema(description = "Localização da sala")
        private String localizacao;

        public static Response fromEntity(Sala sala) {
            return Response.builder()
                    .id(sala.getId())
                    .nome(sala.getNome())
                    .capacidade(sala.getCapacidade())
                    .localizacao(sala.getLocalizacao())
                    .build();
        }
    }
}
