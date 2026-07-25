package com.brenis.em.application.mapper;

import com.brenis.em.application.dto.response.ContratoResponse;
import com.brenis.em.application.dto.response.DetalleContratoResponse;
import com.brenis.em.domain.contrato.Contrato;
import com.brenis.em.domain.contrato.DetalleContrato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContratoMapper {

    @Mapping(target = "eventoId", source = "evento.id")
    @Mapping(target = "eventoTipo", source = "evento.tipoEvento")
    @Mapping(target = "paqueteId", source = "paquete.id")
    @Mapping(target = "paqueteNombre", source = "paquete.nombre")
    @Mapping(target = "detalles", source = "detalles")
    ContratoResponse toResponse(Contrato contrato);

    @Mapping(target = "inventarioId", source = "inventario.id")
    @Mapping(target = "inventarioNombre", source = "inventario.nombre")
    DetalleContratoResponse toDetalleResponse(DetalleContrato detalleContrato);

    List<ContratoResponse> toResponseList(List<Contrato> contratos);
}
