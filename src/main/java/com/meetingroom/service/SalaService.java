package com.meetingroom.service;

import com.meetingroom.dto.SalaDTO;
import com.meetingroom.entity.Sala;
import com.meetingroom.exception.BusinessException;
import com.meetingroom.exception.ResourceNotFoundException;
import com.meetingroom.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaService {

    private final SalaRepository salaRepository;

    @Transactional
    @CacheEvict(value = "salas", allEntries = true)
    public SalaDTO.Response criar(SalaDTO.Request request) {
        log.info("Criando nova sala: {}", request.getNome());

        if (salaRepository.existsByNome(request.getNome())) {
            throw new BusinessException("Já existe uma sala com o nome: " + request.getNome());
        }

        Sala sala = Sala.builder()
                .nome(request.getNome())
                .capacidade(request.getCapacidade())
                .localizacao(request.getLocalizacao())
                .build();

        Sala salaSalva = salaRepository.save(sala);
        log.info("Sala criada com ID: {}", salaSalva.getId());
        return SalaDTO.Response.fromEntity(salaSalva);
    }

    @Cacheable(value = "salas")
    @Transactional(readOnly = true)
    public Page<SalaDTO.Response> listarComFiltros(
            String nome, Integer capacidadeMin, String localizacao, Pageable pageable) {
        log.debug("Listando salas com filtros - nome: {}, capacidadeMin: {}, localizacao: {}",
                  nome, capacidadeMin, localizacao);
        return salaRepository.findWithFilters(nome, capacidadeMin, localizacao, pageable)
                .map(SalaDTO.Response::fromEntity);
    }

    @Transactional(readOnly = true)
    public SalaDTO.Response buscarPorId(Long id) {
        log.debug("Buscando sala por ID: {}", id);
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com ID: " + id));
        return SalaDTO.Response.fromEntity(sala);
    }

    @Transactional
    @CacheEvict(value = "salas", allEntries = true)
    public SalaDTO.Response atualizar(Long id, SalaDTO.Request request) {
        log.info("Atualizando sala ID: {}", id);
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com ID: " + id));

        if (!sala.getNome().equals(request.getNome()) && salaRepository.existsByNome(request.getNome())) {
            throw new BusinessException("Já existe uma sala com o nome: " + request.getNome());
        }

        sala.setNome(request.getNome());
        sala.setCapacidade(request.getCapacidade());
        sala.setLocalizacao(request.getLocalizacao());

        Sala salaAtualizada = salaRepository.save(sala);
        log.info("Sala ID {} atualizada com sucesso", id);
        return SalaDTO.Response.fromEntity(salaAtualizada);
    }

    @Transactional
    @CacheEvict(value = "salas", allEntries = true)
    public void remover(Long id) {
        log.info("Removendo sala ID: {}", id);
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com ID: " + id));
        salaRepository.delete(sala);
        log.info("Sala ID {} removida com sucesso", id);
    }
}
