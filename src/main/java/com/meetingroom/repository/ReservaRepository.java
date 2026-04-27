package com.meetingroom.repository;

import com.meetingroom.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT r FROM Reserva r WHERE r.sala.id = :salaId AND r.status = 'ATIVA' AND " +
           "(:inicio < r.dataHoraFim AND :fim > r.dataHoraInicio)")
    List<Reserva> findConflitantes(
            @Param("salaId") Long salaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("SELECT r FROM Reserva r WHERE r.sala.id = :salaId AND r.status = 'ATIVA' AND " +
           "(:inicio < r.dataHoraFim AND :fim > r.dataHoraInicio) AND r.id <> :reservaId")
    List<Reserva> findConflitantesExcluindoId(
            @Param("salaId") Long salaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("reservaId") Long reservaId);

    @Query("SELECT r FROM Reserva r WHERE " +
           "(:salaId IS NULL OR r.sala.id = :salaId) AND " +
           "(:responsavel IS NULL OR LOWER(r.responsavel) LIKE LOWER(CONCAT('%', :responsavel, '%'))) AND " +
           "(:dataInicio IS NULL OR r.dataHoraInicio >= :dataInicio) AND " +
           "(:dataFim IS NULL OR r.dataHoraFim <= :dataFim) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<Reserva> findWithFilters(
            @Param("salaId") Long salaId,
            @Param("responsavel") String responsavel,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("status") Reserva.StatusReserva status,
            Pageable pageable);
}
