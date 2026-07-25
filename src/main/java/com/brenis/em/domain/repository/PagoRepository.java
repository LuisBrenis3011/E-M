package com.brenis.em.domain.repository;

import com.brenis.em.domain.enums.EstadoPago;
import com.brenis.em.domain.pago.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByContratoId(Long contratoId);

    List<Pago> findByEstado(EstadoPago estado);

    List<Pago> findByContratoIdOrderByFechaPagoDesc(Long contratoId);
}
