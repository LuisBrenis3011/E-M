package com.brenis.em.domain.repository;

import com.brenis.em.domain.contrato.Contrato;
import com.brenis.em.domain.enums.EstadoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    Optional<Contrato> findByEventoId(Long eventoId);

    List<Contrato> findByProveedorId(Long proveedorId);

    List<Contrato> findByProveedorIdAndEstado(Long proveedorId, EstadoContrato estado);

    @Query("SELECT COALESCE(SUM(c.montoTotal), 0) FROM Contrato c " +
           "WHERE c.proveedor.id = :proveedorId AND c.estado IN ('CONFIRMADO', 'COMPLETADO')")
    java.math.BigDecimal sumIngresosTotales(@Param("proveedorId") Long proveedorId);

    List<Contrato> findByProveedorIdAndFechaCreacionBetween(
            Long proveedorId, LocalDateTime desde, LocalDateTime hasta);
}
