package com.brenis.em.domain.repository;

import com.brenis.em.domain.cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByProveedorId(Long proveedorId);

    Optional<Cliente> findByDniAndProveedorId(String dni, Long proveedorId);

    List<Cliente> findByProveedorIdAndNombreCompletoContainingIgnoreCase(Long proveedorId, String nombre);
}
