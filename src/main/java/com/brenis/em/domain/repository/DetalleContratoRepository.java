package com.brenis.em.domain.repository;

import com.brenis.em.domain.contrato.DetalleContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleContratoRepository extends JpaRepository<DetalleContrato, Long> {

    List<DetalleContrato> findByContratoIdOrderByOrden(Long contratoId);

    void deleteByContratoId(Long contratoId);
}
