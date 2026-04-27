package com.meetingroom.service;

import com.meetingroom.dto.ReservaDTO;
import com.meetingroom.entity.Reserva;
import com.meetingroom.entity.Sala;
import com.meetingroom.exception.BusinessException;
import com.meetingroom.exception.ResourceNotFoundException;
import com.meetingroom.exception.ReservaConflitanteException;
import com.meetingroom.repository.ReservaRepository;
import com.meetingroom.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;

    @Transactional
    public ReservaDTO.Response criar(ReservaDTO.Request request) {
        log.info("Criando reserva para sala ID: {} - responsável: {}", request.getSalaId(), request.getResponsavel());

        validarIntervalo(request.getDataHoraInicio(), request.getDataHoraFim());

        Sala sala = salaRepository.findById(request.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com ID: " + request.getSalaId()));

        verificarConflito(request.getSalaId(), request.getDataHoraInicio(), request.getDataHoraFim(), null);

        Reserva reserva = Reserva.builder()
                .sala(sala)
                .dataHoraInicio(request.getDataHoraInicio())
                .dataHoraFim(request.getDataHoraFim())
                .responsavel(request.getResponsavel())
                .status(Reserva.StatusReserva.ATIVA)
                .build();

        Reserva reservaSalva = reservaRepository.save(reserva);
        log.info("Reserva criada com ID: {}", reservaSalva.getId());
        return ReservaDTO.Response.fromEntity(reservaSalva);
    }

    @Transactional(readOnly = true)
    public Page<ReservaDTO.Response> listarComFiltros(
            Long salaId, String responsavel,
            LocalDateTime dataInicio, LocalDateTime dataFim,
            Reserva.StatusReserva status, Pageable pageable) {
        log.debug("Listando reservas com filtros");
        return reservaRepository.findWithFilters(salaId, responsavel, dataInicio, dataFim, status, pageable)
                .map(ReservaDTO.Response::fromEntity);
    }

    @Transactional(readOnly = true)
    public ReservaDTO.Response buscarPorId(Long id) {
        log.debug("Buscando reserva por ID: {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));
        return ReservaDTO.Response.fromEntity(reserva);
    }

    @Transactional
    public ReservaDTO.Response cancelar(Long id) {
        log.info("Cancelando reserva ID: {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));

        if (reserva.getStatus() == Reserva.StatusReserva.CANCELADA) {
            throw new BusinessException("A reserva já está cancelada");
        }

        reserva.setStatus(Reserva.StatusReserva.CANCELADA);
        Reserva reservaCancelada = reservaRepository.save(reserva);
        log.info("Reserva ID {} cancelada com sucesso", id);
        return ReservaDTO.Response.fromEntity(reservaCancelada);
    }

    // ==================== REGRAS DE NEGÓCIO ====================

    public void validarIntervalo(LocalDateTime inicio, LocalDateTime fim) {
        if (!fim.isAfter(inicio)) {
            throw new BusinessException("A data/hora de fim deve ser posterior à data/hora de início");
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new BusinessException("A data/hora de início não pode estar no passado");
        }
    }

    public void verificarConflito(Long salaId, LocalDateTime inicio, LocalDateTime fim, Long reservaIdExcluir) {
        List<Reserva> conflitantes;

        if (reservaIdExcluir != null) {
            conflitantes = reservaRepository.findConflitantesExcluindoId(salaId, inicio, fim, reservaIdExcluir);
        } else {
            conflitantes = reservaRepository.findConflitantes(salaId, inicio, fim);
        }

        if (!conflitantes.isEmpty()) {
            Reserva conflito = conflitantes.get(0);
            throw new ReservaConflitanteException(
                String.format("Conflito de reserva: a sala já está reservada de %s até %s por %s",
                    conflito.getDataHoraInicio(), conflito.getDataHoraFim(), conflito.getResponsavel())
            );
        }
    }
}
