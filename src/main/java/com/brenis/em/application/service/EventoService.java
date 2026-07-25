package com.brenis.em.application.service;

import com.brenis.em.domain.enums.EstadoEvento;
import com.brenis.em.domain.evento.Evento;
import com.brenis.em.domain.repository.*;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EventoService {

    private final EventoRepository eventoRepository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final TematicaRepository tematicaRepository;

    public EventoService(EventoRepository eventoRepository,
                         ClienteRepository clienteRepository,
                         CategoriaRepository categoriaRepository,
                         TematicaRepository tematicaRepository) {
        this.eventoRepository = eventoRepository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.tematicaRepository = tematicaRepository;
    }

    public Evento create(Evento evento) {
        evento.setCliente(clienteRepository.findById(evento.getCliente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente",
                        evento.getCliente().getId())));

        evento.setCategoria(categoriaRepository.findById(evento.getCategoria().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria",
                        evento.getCategoria().getId())));

        if (evento.getTematica() != null && evento.getTematica().getId() != null) {
            evento.setTematica(tematicaRepository.findById(evento.getTematica().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tematica",
                            evento.getTematica().getId())));
        }

        return eventoRepository.save(evento);
    }

    public Evento update(Long id, Evento datos) {
        Evento existente = getById(id);
        existente.setTipoEvento(datos.getTipoEvento());
        existente.setNombreCumpleanero(datos.getNombreCumpleanero());
        existente.setEdadCumpleanero(datos.getEdadCumpleanero());
        existente.setFechaEvento(datos.getFechaEvento());
        existente.setHoraInicio(datos.getHoraInicio());
        existente.setHoraFinEstimada(datos.getHoraFinEstimada());
        existente.setDireccion(datos.getDireccion());
        existente.setReferencia(datos.getReferencia());
        existente.setAforoEstimado(datos.getAforoEstimado());
        existente.setColorCalendario(datos.getColorCalendario());
        existente.setNotasInternas(datos.getNotasInternas());

        if (datos.getCategoria() != null && datos.getCategoria().getId() != null) {
            existente.setCategoria(categoriaRepository.findById(datos.getCategoria().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria",
                            datos.getCategoria().getId())));
        }

        if (datos.getTematica() != null && datos.getTematica().getId() != null) {
            existente.setTematica(tematicaRepository.findById(datos.getTematica().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tematica",
                            datos.getTematica().getId())));
        } else {
            existente.setTematica(null);
        }

        return eventoRepository.save(existente);
    }

    public Evento getById(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    public List<Evento> getCalendario(Long proveedorId, LocalDate inicio, LocalDate fin) {
        return eventoRepository.findCalendario(inicio, fin);
    }

    public List<Evento> getByCliente(Long clienteId) {
        return eventoRepository.findByClienteId(clienteId);
    }

    public Evento cambiarEstado(Long id, EstadoEvento nuevoEstado) {
        Evento evento = getById(id);
        evento.setEstado(nuevoEstado);
        return eventoRepository.save(evento);
    }

    public void delete(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento", id);
        }
        eventoRepository.deleteById(id);
    }
}
