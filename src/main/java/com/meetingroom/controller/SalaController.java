package com.meetingroom.controller;

import com.meetingroom.dto.SalaDTO;
import com.meetingroom.service.SalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Salas de Reunião", description = "CRUD completo de salas de reunião com paginação e filtros")
@SecurityRequirement(name = "bearerAuth")
public class SalaController {

    private final SalaService salaService;

    @PostMapping
    @Operation(summary = "Criar sala", description = "Cria uma nova sala de reunião")
    public ResponseEntity<SalaDTO.Response> criar(@Valid @RequestBody SalaDTO.Request request) {
        log.info("POST /api/salas - criando sala: {}", request.getNome());
        SalaDTO.Response response = salaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
        summary = "Listar salas",
        description = "Lista todas as salas com suporte a paginação e filtros opcionais"
    )
    public ResponseEntity<Page<SalaDTO.Response>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer capacidadeMin,
            @RequestParam(required = false) String localizacao,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        log.debug("GET /api/salas - filtros: nome={}, capacidadeMin={}, localizacao={}", nome, capacidadeMin, localizacao);
        Page<SalaDTO.Response> response = salaService.listarComFiltros(nome, capacidadeMin, localizacao, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sala por ID", description = "Retorna os dados de uma sala específica")
    public ResponseEntity<SalaDTO.Response> buscarPorId(@PathVariable Long id) {
        log.debug("GET /api/salas/{}", id);
        SalaDTO.Response response = salaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar sala", description = "Atualiza os dados de uma sala existente")
    public ResponseEntity<SalaDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalaDTO.Request request) {
        log.info("PUT /api/salas/{}", id);
        SalaDTO.Response response = salaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover sala", description = "Remove uma sala de reunião pelo ID")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        log.info("DELETE /api/salas/{}", id);
        salaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
