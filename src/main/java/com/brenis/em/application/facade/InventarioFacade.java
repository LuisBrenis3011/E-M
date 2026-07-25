package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.InventarioRequest;
import com.brenis.em.application.dto.response.InventarioResponse;
import com.brenis.em.application.mapper.InventarioMapper;
import com.brenis.em.application.service.IInventarioService;
import com.brenis.em.domain.inventario.Inventario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventarioFacade {

    private final IInventarioService inventarioService;
    private final InventarioMapper inventarioMapper;

    public InventarioFacade(IInventarioService inventarioService,
                            InventarioMapper inventarioMapper) {
        this.inventarioService = inventarioService;
        this.inventarioMapper = inventarioMapper;
    }

    public InventarioResponse create(Long proveedorId, InventarioRequest request) {
        Inventario entity = inventarioMapper.toEntity(request);
        Inventario saved = inventarioService.create(proveedorId, entity);
        return inventarioMapper.toResponse(saved);
    }

    public InventarioResponse update(Long id, InventarioRequest request) {
        Inventario entity = inventarioMapper.toEntity(request);
        Inventario updated = inventarioService.update(id, entity);
        return inventarioMapper.toResponse(updated);
    }

    public InventarioResponse findById(Long id) {
        return inventarioMapper.toResponse(inventarioService.findById(id));
    }

    public List<InventarioResponse> findAllByProveedor(Long proveedorId) {
        return inventarioMapper.toResponseList(inventarioService.findAllByProveedor(proveedorId));
    }

    public List<InventarioResponse> search(Long proveedorId, String query) {
        return inventarioMapper.toResponseList(inventarioService.search(proveedorId, query));
    }

    public void deactivate(Long id) {
        inventarioService.deactivate(id);
    }

    public void deleteById(Long id) {
        inventarioService.deleteById(id);
    }
}
