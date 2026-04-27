package com.meetingroom.controller;

import com.meetingroom.dto.ReservaDTO;
import com.meetingroom.entity.Reserva;
import com.meetingroom.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservas", description = "Gerenciamento de reservas de salas com verificação de conflitos")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(
        summary = "Criar reserva",
        description = "Cria uma nova reserva. Valida conflitos de horário e intervalo de datas."
    )
    public ResponseEntity<ReservaDTO.Response> criar(@Valid @RequestBody ReservaDTO.Request request) {
        log.info("POST /api/reservas - sala: {}, responsável: {}", request.getSalaId(), request.getResponsavel());
        ReservaDTO.Response response = reservaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
        summary = "Listar reservas",
        description = "Lista reservas com filtros opcionais: sala, responsável, período e status"
    )
    public ResponseEntity<Page<ReservaDTO.Response>> listar(
            @RequestParam(required = false) Long salaId,
            @RequestParam(required = false) String responsavel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) Reserva.StatusReserva status,
            @PageableDefault(size = 10, sort = "dataHoraInicio") Pageable pageable) {
        log.debug("GET /api/reservas com filtros");
        Page<ReservaDTO.Response> response = reservaService.listarComFiltros(
                salaId, responsavel, dataInicio, dataFim, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID", description = "Retorna os dados de uma reserva específica")
    public ResponseEntity<ReservaDTO.Response> buscarPorId(@PathVariable Long id) {
        log.debug("GET /api/reservas/{}", id);
        ReservaDTO.Response response = reservaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva", description = "Cancela uma reserva ativa pelo ID")
    public ResponseEntity<ReservaDTO.Response> cancelar(@PathVariable Long id) {
        log.info("PATCH /api/reservas/{}/cancelar", id);
        ReservaDTO.Response response = reservaService.cancelar(id);
        return ResponseEntity.ok(response);
    }
}
