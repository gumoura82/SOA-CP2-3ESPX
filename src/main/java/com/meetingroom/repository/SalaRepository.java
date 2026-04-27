package com.meetingroom.repository;

import com.meetingroom.entity.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByNome(String nome);

    @Query("SELECT s FROM Sala s WHERE " +
           "(:nome IS NULL OR LOWER(s.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:capacidadeMin IS NULL OR s.capacidade >= :capacidadeMin) AND " +
           "(:localizacao IS NULL OR LOWER(s.localizacao) LIKE LOWER(CONCAT('%', :localizacao, '%')))")
    Page<Sala> findWithFilters(
            @Param("nome") String nome,
            @Param("capacidadeMin") Integer capacidadeMin,
            @Param("localizacao") String localizacao,
            Pageable pageable);
}
