package com.brenis.em.application.service;

import com.brenis.em.domain.cliente.Cliente;

import java.util.List;

public interface IClienteService {

    Cliente create(Long proveedorId, Cliente cliente);

    Cliente update(Long id, Cliente datos);

    Cliente findById(Long id);

    List<Cliente> findAllByProveedor(Long proveedorId);

    List<Cliente> search(Long proveedorId, String query);

    java.util.Optional<Cliente> findByDni(Long proveedorId, String dni);

    void deleteById(Long id);
}
