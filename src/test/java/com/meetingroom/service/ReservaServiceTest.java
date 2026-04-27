package com.meetingroom.service;

import com.meetingroom.dto.ReservaDTO;
import com.meetingroom.entity.Reserva;
import com.meetingroom.entity.Sala;
import com.meetingroom.exception.BusinessException;
import com.meetingroom.exception.ReservaConflitanteException;
import com.meetingroom.exception.ResourceNotFoundException;
import com.meetingroom.repository.ReservaRepository;
import com.meetingroom.repository.SalaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ReservaService")
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Sala salaMock;
    private LocalDateTime inicio;
    private LocalDateTime fim;

    @BeforeEach
    void setUp() {
        salaMock = Sala.builder()
                .id(1L)
                .nome("Sala Alfa")
                .capacidade(10)
                .localizacao("Andar 1")
                .build();

        inicio = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
        fim = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
    }

    // ===== TESTE 1: Conflito de reserva =====

    @Test
    @DisplayName("Deve lançar exceção ao criar reserva com conflito de horário")
    void deveLancarExcecaoQuandoHouverConflito() {
        // Arrange
        ReservaDTO.Request request = ReservaDTO.Request.builder()
                .salaId(1L)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .responsavel("João Silva")
                .build();

        Reserva reservaExistente = Reserva.builder()
                .id(99L)
                .sala(salaMock)
                .dataHoraInicio(inicio.minusMinutes(30))
                .dataHoraFim(fim.minusMinutes(30))
                .responsavel("Maria Souza")
                .status(Reserva.StatusReserva.ATIVA)
                .build();

        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaMock));
        when(reservaRepository.findConflitantes(eq(1L), any(), any()))
                .thenReturn(List.of(reservaExistente));

        // Act & Assert
        assertThatThrownBy(() -> reservaService.criar(request))
                .isInstanceOf(ReservaConflitanteException.class)
                .hasMessageContaining("Conflito de reserva");

        verify(reservaRepository, never()).save(any());
    }

    // ===== TESTE 2: Criação bem-sucedida =====

    @Test
    @DisplayName("Deve criar reserva com sucesso quando não há conflito")
    void deveCriarReservaComSucesso() {
        // Arrange
        ReservaDTO.Request request = ReservaDTO.Request.builder()
                .salaId(1L)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .responsavel("Carlos Oliveira")
                .build();

        Reserva reservaSalva = Reserva.builder()
                .id(1L)
                .sala(salaMock)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .responsavel("Carlos Oliveira")
                .status(Reserva.StatusReserva.ATIVA)
                .build();

        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaMock));
        when(reservaRepository.findConflitantes(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaSalva);

        // Act
        ReservaDTO.Response response = reservaService.criar(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getResponsavel()).isEqualTo("Carlos Oliveira");
        assertThat(response.getStatus()).isEqualTo(Reserva.StatusReserva.ATIVA);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    // ===== TESTE 3: Intervalo de datas inválido =====

    @Test
    @DisplayName("Deve lançar exceção quando data fim for anterior à data início")
    void deveLancarExcecaoQuandoIntervaloInvalido() {
        // Arrange
        LocalDateTime inicioInvalido = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime fimInvalido = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0); // fim < início

        // Act & Assert
        assertThatThrownBy(() -> reservaService.validarIntervalo(inicioInvalido, fimInvalido))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("posterior");
    }

    // ===== TESTE 4: Cancelamento de reserva já cancelada =====

    @Test
    @DisplayName("Deve lançar exceção ao cancelar reserva já cancelada")
    void deveLancarExcecaoAoCancelarReservaJaCancelada() {
        // Arrange
        Reserva reservaCancelada = Reserva.builder()
                .id(1L)
                .sala(salaMock)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .responsavel("Ana Lima")
                .status(Reserva.StatusReserva.CANCELADA)
                .build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaCancelada));

        // Act & Assert
        assertThatThrownBy(() -> reservaService.cancelar(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já está cancelada");
    }

    // ===== TESTE 5: Sala não encontrada =====

    @Test
    @DisplayName("Deve lançar exceção quando sala não for encontrada ao criar reserva")
    void deveLancarExcecaoQuandoSalaNaoEncontrada() {
        // Arrange
        ReservaDTO.Request request = ReservaDTO.Request.builder()
                .salaId(999L)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .responsavel("Responsável")
                .build();

        when(salaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservaService.criar(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sala não encontrada");
    }

    // ===== TESTE 6: Cancelamento bem-sucedido =====

    @Test
    @DisplayName("Deve cancelar reserva ativa com sucesso")
    void deveCancelarReservaAtiva() {
        // Arrange
        Reserva reservaAtiva = Reserva.builder()
                .id(1L)
                .sala(salaMock)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .responsavel("Pedro Costa")
                .status(Reserva.StatusReserva.ATIVA)
                .build();

        Reserva reservaCancelada = Reserva.builder()
                .id(1L)
                .sala(salaMock)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .responsavel("Pedro Costa")
                .status(Reserva.StatusReserva.CANCELADA)
                .build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaAtiva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaCancelada);

        // Act
        ReservaDTO.Response response = reservaService.cancelar(1L);

        // Assert
        assertThat(response.getStatus()).isEqualTo(Reserva.StatusReserva.CANCELADA);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }
}
