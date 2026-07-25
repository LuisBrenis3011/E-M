package com.brenis.em.domain.repository;

import com.brenis.em.domain.evento.Evento;
import com.brenis.em.domain.enums.EstadoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByClienteId(Long clienteId);

    List<Evento> findByFechaEventoBetweenOrderByFechaEventoAscHoraInicioAsc(
            LocalDate inicio, LocalDate fin);

    @Query("SELECT e FROM Evento e WHERE e.fechaEvento BETWEEN :inicio AND :fin " +
           "ORDER BY e.fechaEvento, e.horaInicio")
    List<Evento> findCalendario(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    List<Evento> findByEstado(EstadoEvento estado);

    long countByEstado(EstadoEvento estado);
}
