package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.EventoRequest;
import com.brenis.em.application.dto.response.EventoResponse;
import com.brenis.em.application.mapper.EventoMapper;
import com.brenis.em.application.service.IEventoService;
import com.brenis.em.domain.categoria.Categoria;
import com.brenis.em.domain.cliente.Cliente;
import com.brenis.em.domain.enums.EstadoEvento;
import com.brenis.em.domain.evento.Evento;
import com.brenis.em.domain.paquete.Paquete;
import com.brenis.em.domain.tematica.Tematica;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventoFacade {

    private final IEventoService eventoService;
    private final EventoMapper eventoMapper;

    public EventoFacade(IEventoService eventoService, EventoMapper eventoMapper) {
        this.eventoService = eventoService;
        this.eventoMapper = eventoMapper;
    }

    public EventoResponse create(EventoRequest request) {
        Evento evento = Evento.builder()
                .cliente(Cliente.builder().id(request.getClienteId()).build())
                .categoria(Categoria.builder().id(request.getCategoriaId()).build())
                .tipoEvento(request.getTipoEvento())
                .nombreCumpleanero(request.getNombreCumpleanero())
                .edadCumpleanero(request.getEdadCumpleanero())
                .fechaEvento(request.getFechaEvento())
                .horaInicio(request.getHoraInicio())
                .horaFinEstimada(request.getHoraFinEstimada())
                .direccion(request.getDireccion())
                .referencia(request.getReferencia())
                .aforoEstimado(request.getAforoEstimado())
                .colorCalendario(request.getColorCalendario())
                .notasInternas(request.getNotasInternas())
                .build();

        if (request.getTematicaId() != null) {
            evento.setTematica(Tematica.builder().id(request.getTematicaId()).build());
        }

        evento.setPaquete(Paquete.builder().id(request.getPaqueteId()).build());

        return eventoMapper.toResponse(eventoService.create(evento));
    }

    public EventoResponse update(Long id, EventoRequest request) {
        Evento evento = Evento.builder()
                .tipoEvento(request.getTipoEvento())
                .nombreCumpleanero(request.getNombreCumpleanero())
                .edadCumpleanero(request.getEdadCumpleanero())
                .fechaEvento(request.getFechaEvento())
                .horaInicio(request.getHoraInicio())
                .horaFinEstimada(request.getHoraFinEstimada())
                .direccion(request.getDireccion())
                .referencia(request.getReferencia())
                .aforoEstimado(request.getAforoEstimado())
                .colorCalendario(request.getColorCalendario())
                .notasInternas(request.getNotasInternas())
                .categoria(Categoria.builder().id(request.getCategoriaId()).build())
                .build();

        if (request.getTematicaId() != null) {
            evento.setTematica(Tematica.builder().id(request.getTematicaId()).build());
        }

        evento.setPaquete(Paquete.builder().id(request.getPaqueteId()).build());

        return eventoMapper.toResponse(eventoService.update(id, evento));
    }

    public EventoResponse findById(Long id) {
        return eventoMapper.toResponse(eventoService.findById(id));
    }

    public List<EventoResponse> findCalendario(LocalDate inicio, LocalDate fin) {
        return eventoMapper.toResponseList(eventoService.findCalendario(inicio, fin));
    }

    public List<EventoResponse> findByCliente(Long clienteId) {
        return eventoMapper.toResponseList(eventoService.findByCliente(clienteId));
    }

    public EventoResponse cambiarEstado(Long id, EstadoEvento estado) {
        return eventoMapper.toResponse(eventoService.cambiarEstado(id, estado));
    }

    public void deleteById(Long id) {
        eventoService.deleteById(id);
    }
}
