package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.ClienteRequest;
import com.brenis.em.application.dto.response.ClienteResponse;
import com.brenis.em.application.mapper.ClienteMapper;
import com.brenis.em.application.service.ClienteService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClienteFacade {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    public ClienteFacade(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    public ClienteResponse create(Long proveedorId, ClienteRequest request) {
        var entity = clienteMapper.toEntity(request);
        var saved = clienteService.create(proveedorId, entity);
        return clienteMapper.toResponse(saved);
    }

    public ClienteResponse update(Long id, ClienteRequest request) {
        var entity = clienteMapper.toEntity(request);
        var updated = clienteService.update(id, entity);
        return clienteMapper.toResponse(updated);
    }

    public ClienteResponse getById(Long id) {
        return clienteMapper.toResponse(clienteService.getById(id));
    }

    public List<ClienteResponse> getAllByProveedor(Long proveedorId) {
        return clienteMapper.toResponseList(clienteService.getAllByProveedor(proveedorId));
    }

    public List<ClienteResponse> search(Long proveedorId, String query) {
        return clienteMapper.toResponseList(clienteService.search(proveedorId, query));
    }

    public void delete(Long id) {
        clienteService.delete(id);
    }
}
