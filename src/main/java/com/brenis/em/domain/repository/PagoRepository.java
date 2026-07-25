package com.brenis.em.domain.repository;

import com.brenis.em.domain.enums.EstadoPago;
import com.brenis.em.domain.pago.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByContratoId(Long contratoId);

    List<Pago> findByEstado(EstadoPago estado);

    long countByEstado(EstadoPago estado);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.estado = 'PENDIENTE'")
    BigDecimal sumMontoPendiente();

    List<Pago> findByContratoIdOrderByFechaPagoDesc(Long contratoId);
}
