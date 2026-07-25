package com.brenis.em.domain.repository;

import com.brenis.em.domain.paquete.Paquete;
import com.brenis.em.domain.enums.EstadoBasico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

    List<Paquete> findByProveedorId(Long proveedorId);

    List<Paquete> findByProveedorIdAndEstado(Long proveedorId, EstadoBasico estado);

    List<Paquete> findByCategoriaId(Long categoriaId);
}
