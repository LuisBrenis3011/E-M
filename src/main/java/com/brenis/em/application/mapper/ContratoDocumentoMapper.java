package com.brenis.em.application.mapper;

import com.brenis.em.application.dto.response.ContratoDocumentoResponse;
import com.brenis.em.domain.documento.ContratoDocumento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContratoDocumentoMapper {

    @Mapping(target = "contratoId", source = "contrato.id")
    @Mapping(target = "plantillaId", source = "plantilla.id")
    ContratoDocumentoResponse toResponse(ContratoDocumento documento);

    List<ContratoDocumentoResponse> toResponseList(List<ContratoDocumento> documentos);
}
