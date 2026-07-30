package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.IEventoService;
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
public class EventoServiceImpl implements IEventoService {

    private final EventoRepository eventoRepository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final TematicaRepository tematicaRepository;
    private final PaqueteRepository paqueteRepository;

    public EventoServiceImpl(EventoRepository eventoRepository,
                             ClienteRepository clienteRepository,
                             CategoriaRepository categoriaRepository,
                             TematicaRepository tematicaRepository,
                             PaqueteRepository paqueteRepository) {
        this.eventoRepository = eventoRepository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.tematicaRepository = tematicaRepository;
        this.paqueteRepository = paqueteRepository;
    }

    @Override
    public Evento create(Evento evento) {
        evento.setCliente(clienteRepository.findById(evento.getCliente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", evento.getCliente().getId())));

        evento.setCategoria(categoriaRepository.findById(evento.getCategoria().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", evento.getCategoria().getId())));

        if (evento.getTematica() != null && evento.getTematica().getId() != null) {
            evento.setTematica(tematicaRepository.findById(evento.getTematica().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tematica", evento.getTematica().getId())));
        }

        if (evento.getPaquete() != null && evento.getPaquete().getId() != null) {
            evento.setPaquete(paqueteRepository.findById(evento.getPaquete().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Paquete", evento.getPaquete().getId())));
        }

        return eventoRepository.save(evento);
    }

    @Override
    public Evento update(Long id, Evento datos) {
        Evento existente = findById(id);
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
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", datos.getCategoria().getId())));
        }

        if (datos.getTematica() != null && datos.getTematica().getId() != null) {
            existente.setTematica(tematicaRepository.findById(datos.getTematica().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tematica", datos.getTematica().getId())));
        } else {
            existente.setTematica(null);
        }

        if (datos.getPaquete() != null && datos.getPaquete().getId() != null) {
            existente.setPaquete(paqueteRepository.findById(datos.getPaquete().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Paquete", datos.getPaquete().getId())));
        }

        return eventoRepository.save(existente);
    }

    @Override
    public Evento findById(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    @Override
    public List<Evento> findCalendario(LocalDate inicio, LocalDate fin) {
        return eventoRepository.findCalendario(inicio, fin);
    }

    @Override
    public List<Evento> findByCliente(Long clienteId) {
        return eventoRepository.findByClienteId(clienteId);
    }

    @Override
    public Evento cambiarEstado(Long id, EstadoEvento nuevoEstado) {
        Evento evento = findById(id);
        evento.setEstado(nuevoEstado);
        return eventoRepository.save(evento);
    }

    @Override
    public void deleteById(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento", id);
        }
        eventoRepository.deleteById(id);
    }
}
