package com.meetingroom.service;

import com.meetingroom.dto.SalaDTO;
import com.meetingroom.entity.Sala;
import com.meetingroom.exception.BusinessException;
import com.meetingroom.exception.ResourceNotFoundException;
import com.meetingroom.repository.SalaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - SalaService")
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    @Test
    @DisplayName("Deve criar sala com sucesso")
    void deveCriarSalaComSucesso() {
        // Arrange
        SalaDTO.Request request = SalaDTO.Request.builder()
                .nome("Sala de Inovação")
                .capacidade(15)
                .localizacao("Andar 5 - Ala Oeste")
                .build();

        Sala salaSalva = Sala.builder()
                .id(1L)
                .nome("Sala de Inovação")
                .capacidade(15)
                .localizacao("Andar 5 - Ala Oeste")
                .build();

        when(salaRepository.existsByNome("Sala de Inovação")).thenReturn(false);
        when(salaRepository.save(any(Sala.class))).thenReturn(salaSalva);

        // Act
        SalaDTO.Response response = salaService.criar(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Sala de Inovação");
        assertThat(response.getCapacidade()).isEqualTo(15);
        verify(salaRepository, times(1)).save(any(Sala.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar sala com nome duplicado")
    void deveLancarExcecaoNomeDuplicado() {
        // Arrange
        SalaDTO.Request request = SalaDTO.Request.builder()
                .nome("Sala Alfa")
                .capacidade(10)
                .localizacao("Andar 1")
                .build();

        when(salaRepository.existsByNome("Sala Alfa")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> salaService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe uma sala com o nome");

        verify(salaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar sala inexistente")
    void deveLancarExcecaoBuscarSalaInexistente() {
        // Arrange
        when(salaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> salaService.buscarPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sala não encontrada com ID: 999");
    }

    @Test
    @DisplayName("Deve retornar sala ao buscar por ID existente")
    void deveRetornarSalaPorIdExistente() {
        // Arrange
        Sala sala = Sala.builder()
                .id(1L)
                .nome("Sala Beta")
                .capacidade(20)
                .localizacao("Andar 2")
                .build();

        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));

        // Act
        SalaDTO.Response response = salaService.buscarPorId(1L);

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Sala Beta");
    }

    @Test
    @DisplayName("Deve remover sala existente com sucesso")
    void deveRemoverSalaComSucesso() {
        // Arrange
        Sala sala = Sala.builder().id(1L).nome("Sala para Deletar").capacidade(5).localizacao("Andar 1").build();
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        doNothing().when(salaRepository).delete(sala);

        // Act
        salaService.remover(1L);

        // Assert
        verify(salaRepository, times(1)).delete(sala);
    }
}
