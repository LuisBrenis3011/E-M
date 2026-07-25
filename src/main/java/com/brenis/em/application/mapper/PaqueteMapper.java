package com.brenis.em.application.mapper;

import com.brenis.em.application.dto.response.DetallePaqueteResponse;
import com.brenis.em.application.dto.response.PaqueteResponse;
import com.brenis.em.domain.paquete.DetallePaquete;
import com.brenis.em.domain.paquete.Paquete;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaqueteMapper {

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    @Mapping(target = "tematicaId", source = "tematica.id")
    @Mapping(target = "tematicaNombre", source = "tematica.nombre")
    @Mapping(target = "detalles", source = "detalles")
    PaqueteResponse toResponse(Paquete paquete);

    @Mapping(target = "inventarioId", source = "inventario.id")
    @Mapping(target = "inventarioNombre", source = "inventario.nombre")
    DetallePaqueteResponse toDetalleResponse(DetallePaquete detallePaquete);

    List<PaqueteResponse> toResponseList(List<Paquete> paquetes);
}
