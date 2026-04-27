package com.meetingroom.dto;

import com.meetingroom.entity.Reserva;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReservaDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Dados para criação de reserva")
    public static class Request {

        @NotNull(message = "ID da sala é obrigatório")
        @Schema(description = "ID da sala a ser reservada", example = "1")
        private Long salaId;

        @NotNull(message = "Data/hora de início é obrigatória")
        @Future(message = "Data/hora de início deve ser no futuro")
        @Schema(description = "Data e hora de início", example = "2024-12-01T09:00:00")
        private LocalDateTime dataHoraInicio;

        @NotNull(message = "Data/hora de fim é obrigatória")
        @Future(message = "Data/hora de fim deve ser no futuro")
        @Schema(description = "Data e hora de fim", example = "2024-12-01T10:00:00")
        private LocalDateTime dataHoraFim;

        @NotBlank(message = "Responsável é obrigatório")
        @Schema(description = "Nome do responsável pela reserva", example = "João Silva")
        private String responsavel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Dados de retorno de uma reserva")
    public static class Response {
        private Long id;
        private Long salaId;
        private String salaNome;
        private LocalDateTime dataHoraInicio;
        private LocalDateTime dataHoraFim;
        private String responsavel;
        private Reserva.StatusReserva status;

        public static Response fromEntity(Reserva reserva) {
            return Response.builder()
                    .id(reserva.getId())
                    .salaId(reserva.getSala().getId())
                    .salaNome(reserva.getSala().getNome())
                    .dataHoraInicio(reserva.getDataHoraInicio())
                    .dataHoraFim(reserva.getDataHoraFim())
                    .responsavel(reserva.getResponsavel())
                    .status(reserva.getStatus())
                    .build();
        }
    }
}
