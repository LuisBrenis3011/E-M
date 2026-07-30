package com.brenis.em.application.mapper;

import com.brenis.em.application.dto.response.EventoResponse;
import com.brenis.em.domain.evento.Evento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventoMapper {

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNombre", source = "cliente.nombreCompleto")
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    @Mapping(target = "tematicaId", source = "tematica.id")
    @Mapping(target = "tematicaNombre", source = "tematica.nombre")
    @Mapping(target = "paqueteId", source = "paquete.id")
    @Mapping(target = "paqueteNombre", source = "paquete.nombre")
    @Mapping(target = "paquetePrecio", source = "paquete.precioBase")
    EventoResponse toResponse(Evento evento);

    List<EventoResponse> toResponseList(List<Evento> eventos);
}
