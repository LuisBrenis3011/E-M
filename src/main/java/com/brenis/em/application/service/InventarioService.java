package com.brenis.em.application.service;

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
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProveedorRepository proveedorRepository;

    public InventarioService(InventarioRepository inventarioRepository,
                             ProveedorRepository proveedorRepository) {
        this.inventarioRepository = inventarioRepository;
        this.proveedorRepository = proveedorRepository;
    }

    public Inventario create(Long proveedorId, Inventario item) {
        item.setProveedor(proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", proveedorId)));
        return inventarioRepository.save(item);
    }

    public Inventario update(Long id, Inventario datos) {
        Inventario existente = getById(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setCantidadDisponible(datos.getCantidadDisponible());
        existente.setPrecioReferencial(datos.getPrecioReferencial());
        return inventarioRepository.save(existente);
    }

    public Inventario getById(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", id));
    }

    public List<Inventario> getAllByProveedor(Long proveedorId) {
        return inventarioRepository.findByProveedorIdAndEstado(proveedorId, EstadoBasico.ACTIVO);
    }

    public List<Inventario> search(Long proveedorId, String query) {
        if (query == null || query.isBlank()) {
            return getAllByProveedor(proveedorId);
        }
        return inventarioRepository.searchInventario(proveedorId, query);
    }

    public void deactivate(Long id) {
        Inventario item = getById(id);
        item.setEstado(EstadoBasico.INACTIVO);
        inventarioRepository.save(item);
    }

    public void delete(Long id) {
        if (!inventarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventario", id);
        }
        inventarioRepository.deleteById(id);
    }
}
