package com.nery.java_agendamento.repository;

import com.nery.java_agendamento.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("SELECT a FROM Agendamento a WHERE a.barbeiro.id = :barbeiroId " +
            "AND ((a.dataHoraInicio BETWEEN :inicio AND :fim) " +
            "OR (a.dataHoraFim BETWEEN :inicio AND :fim))")
    List<Agendamento> findConflitos(@Param("barbeiroId") Long barbeiroId,
                                    @Param("inicio") LocalDateTime inicio,
                                    @Param("fim") LocalDateTime fim);
}