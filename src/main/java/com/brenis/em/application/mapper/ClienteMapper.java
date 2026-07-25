package com.brenis.em.application.mapper;

import com.brenis.em.application.dto.request.ClienteRequest;
import com.brenis.em.application.dto.response.ClienteResponse;
import com.brenis.em.domain.cliente.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proveedor", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente cliente);

    List<ClienteResponse> toResponseList(List<Cliente> clientes);
}
