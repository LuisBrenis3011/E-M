package com.brenis.em.application.service;

import com.brenis.em.domain.enums.EstadoEvento;
import com.brenis.em.domain.evento.Evento;

import java.time.LocalDate;
import java.util.List;

public interface IEventoService {

    Evento create(Evento evento);

    Evento update(Long id, Evento datos);

    Evento findById(Long id);

    List<Evento> findCalendario(LocalDate inicio, LocalDate fin);

    List<Evento> findByCliente(Long clienteId);

    Evento cambiarEstado(Long id, EstadoEvento nuevoEstado);

    void deleteById(Long id);
}
