package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.IInventarioService;
import com.brenis.em.domain.enums.EstadoBasico;
import com.brenis.em.domain.inventario.Inventario;
import com.brenis.em.domain.repository.InventarioRepository;
import com.brenis.em.domain.repository.ProveedorRepository;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InventarioServiceImpl implements IInventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProveedorRepository proveedorRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository,
                                 ProveedorRepository proveedorRepository) {
        this.inventarioRepository = inventarioRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public Inventario create(Long proveedorId, Inventario item) {
        item.setProveedor(proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", proveedorId)));
        return inventarioRepository.save(item);
    }

    @Override
    public Inventario update(Long id, Inventario datos) {
        Inventario existente = findById(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setCantidadDisponible(datos.getCantidadDisponible());
        existente.setPrecioReferencial(datos.getPrecioReferencial());
        return inventarioRepository.save(existente);
    }

    @Override
    public Inventario findById(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", id));
    }

    @Override
    public List<Inventario> findAllByProveedor(Long proveedorId) {
        return inventarioRepository.findByProveedorIdAndEstado(proveedorId, EstadoBasico.ACTIVO);
    }

    @Override
    public List<Inventario> search(Long proveedorId, String query) {
        if (query == null || query.isBlank()) {
            return findAllByProveedor(proveedorId);
        }
        return inventarioRepository.searchInventario(proveedorId, query);
    }

    @Override
    public void deactivate(Long id) {
        Inventario item = findById(id);
        item.setEstado(EstadoBasico.INACTIVO);
        inventarioRepository.save(item);
    }

    @Override
    public void deleteById(Long id) {
        if (!inventarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventario", id);
        }
        inventarioRepository.deleteById(id);
    }
}
