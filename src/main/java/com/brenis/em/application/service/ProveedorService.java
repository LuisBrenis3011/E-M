package com.brenis.em.application.service;

import com.brenis.em.domain.proveedor.Proveedor;
import com.brenis.em.domain.repository.ProveedorRepository;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public Proveedor getById(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
    }

    public Proveedor getByRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor con RUC: " + ruc));
    }

    public Proveedor saveOrUpdate(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public Proveedor update(Long id, Proveedor datos) {
        Proveedor existente = getById(id);
        existente.setNombreEmpresa(datos.getNombreEmpresa());
        existente.setRuc(datos.getRuc());
        existente.setNombreGerente(datos.getNombreGerente());
        existente.setDireccion(datos.getDireccion());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());
        existente.setLogoUrl(datos.getLogoUrl());
        existente.setTerminosCondiciones(datos.getTerminosCondiciones());
        return proveedorRepository.save(existente);
    }
}
