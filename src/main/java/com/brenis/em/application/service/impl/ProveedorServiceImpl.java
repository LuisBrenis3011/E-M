package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.IProveedorService;
import com.brenis.em.domain.proveedor.Proveedor;
import com.brenis.em.domain.repository.ProveedorRepository;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProveedorServiceImpl implements IProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public Proveedor findById(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
    }

    @Override
    public Optional<Proveedor> findByRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc);
    }

    @Override
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor update(Long id, Proveedor datos) {
        Proveedor existente = findById(id);
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
