package com.brenis.em.application.mapper;

import com.brenis.em.application.dto.response.PagoResponse;
import com.brenis.em.domain.pago.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(target = "contratoId", source = "contrato.id")
    PagoResponse toResponse(Pago pago);

    List<PagoResponse> toResponseList(List<Pago> pagos);
}
