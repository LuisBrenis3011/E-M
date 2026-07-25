package com.brenis.em.application.service;

import com.brenis.em.domain.proveedor.Proveedor;

import java.util.Optional;

public interface IProveedorService {

    Proveedor findById(Long id);

    Optional<Proveedor> findByRuc(String ruc);

    Proveedor save(Proveedor proveedor);

    Proveedor update(Long id, Proveedor datos);
}
