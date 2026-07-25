package com.brenis.em.application.service;

import com.brenis.em.domain.inventario.Inventario;

import java.util.List;

public interface IInventarioService {

    Inventario create(Long proveedorId, Inventario item);

    Inventario update(Long id, Inventario datos);

    Inventario findById(Long id);

    List<Inventario> findAllByProveedor(Long proveedorId);

    List<Inventario> search(Long proveedorId, String query);

    void deactivate(Long id);

    void deleteById(Long id);
}
