package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.ClienteRequest;
import com.brenis.em.application.dto.response.ClienteResponse;
import com.brenis.em.application.mapper.ClienteMapper;
import com.brenis.em.application.service.IClienteService;
import com.brenis.em.domain.cliente.Cliente;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ClienteFacade {

    private final IClienteService clienteService;
    private final ClienteMapper clienteMapper;

    public ClienteFacade(IClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    public ClienteResponse create(Long proveedorId, ClienteRequest request) {
        Cliente entity = clienteMapper.toEntity(request);
        Cliente saved = clienteService.create(proveedorId, entity);
        return clienteMapper.toResponse(saved);
    }

    public ClienteResponse update(Long id, ClienteRequest request) {
        Cliente entity = clienteMapper.toEntity(request);
        Cliente updated = clienteService.update(id, entity);
        return clienteMapper.toResponse(updated);
    }

    public ClienteResponse findById(Long id) {
        return clienteMapper.toResponse(clienteService.findById(id));
    }

    public List<ClienteResponse> findAllByProveedor(Long proveedorId) {
        return clienteMapper.toResponseList(clienteService.findAllByProveedor(proveedorId));
    }

    public List<ClienteResponse> search(Long proveedorId, String query) {
        return clienteMapper.toResponseList(clienteService.search(proveedorId, query));
    }

    public List<ClienteResponse> searchByDni(Long proveedorId, String dni) {
        return clienteService.findByDni(proveedorId, dni)
                .map(c -> Collections.singletonList(clienteMapper.toResponse(c)))
                .orElse(Collections.emptyList());
    }

    public void deleteById(Long id) {
        clienteService.deleteById(id);
    }
}
