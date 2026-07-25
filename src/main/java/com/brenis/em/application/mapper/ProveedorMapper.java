package com.brenis.em.application.mapper;

import com.brenis.em.application.dto.response.ProveedorResponse;
import com.brenis.em.domain.proveedor.Proveedor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {

    ProveedorResponse toResponse(Proveedor proveedor);
}
