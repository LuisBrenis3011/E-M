package com.brenis.em.application.service;

import com.brenis.em.domain.paquete.DetallePaquete;
import com.brenis.em.domain.paquete.Paquete;

import java.util.List;

public interface IPaqueteService {

    Paquete create(Long proveedorId, Paquete paquete);

    Paquete update(Long id, Paquete datos);

    Paquete findById(Long id);

    List<Paquete> findAllByProveedor(Long proveedorId);

    List<Paquete> findByCategoria(Long categoriaId);

    DetallePaquete addDetalle(Long paqueteId, DetallePaquete detalle);

    void removeDetalle(Long detalleId);

    void deactivate(Long id);

    void deleteById(Long id);
}
