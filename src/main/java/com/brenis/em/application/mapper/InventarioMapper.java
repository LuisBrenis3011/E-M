package com.brenis.em.application.mapper;

import com.brenis.em.application.dto.request.InventarioRequest;
import com.brenis.em.application.dto.response.InventarioResponse;
import com.brenis.em.domain.inventario.Inventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proveedor", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Inventario toEntity(InventarioRequest request);

    InventarioResponse toResponse(Inventario inventario);

    List<InventarioResponse> toResponseList(List<Inventario> inventarios);
}
