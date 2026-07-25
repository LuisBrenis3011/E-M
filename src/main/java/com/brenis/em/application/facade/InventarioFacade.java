package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.InventarioRequest;
import com.brenis.em.application.dto.response.InventarioResponse;
import com.brenis.em.application.mapper.InventarioMapper;
import com.brenis.em.application.service.InventarioService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventarioFacade {

    private final InventarioService inventarioService;
    private final InventarioMapper inventarioMapper;

    public InventarioFacade(InventarioService inventarioService,
                            InventarioMapper inventarioMapper) {
        this.inventarioService = inventarioService;
        this.inventarioMapper = inventarioMapper;
    }

    public InventarioResponse create(Long proveedorId, InventarioRequest request) {
        var entity = inventarioMapper.toEntity(request);
        var saved = inventarioService.create(proveedorId, entity);
        return inventarioMapper.toResponse(saved);
    }

    public InventarioResponse update(Long id, InventarioRequest request) {
        var entity = inventarioMapper.toEntity(request);
        var updated = inventarioService.update(id, entity);
        return inventarioMapper.toResponse(updated);
    }

    public InventarioResponse getById(Long id) {
        return inventarioMapper.toResponse(inventarioService.getById(id));
    }

    public List<InventarioResponse> getAllByProveedor(Long proveedorId) {
        return inventarioMapper.toResponseList(inventarioService.getAllByProveedor(proveedorId));
    }

    public List<InventarioResponse> search(Long proveedorId, String query) {
        return inventarioMapper.toResponseList(inventarioService.search(proveedorId, query));
    }

    public void deactivate(Long id) {
        inventarioService.deactivate(id);
    }

    public void delete(Long id) {
        inventarioService.delete(id);
    }
}
