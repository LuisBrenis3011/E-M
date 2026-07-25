package com.brenis.em.domain.repository;

import com.brenis.em.domain.enums.EstadoBasico;
import com.brenis.em.domain.plantilla.PlantillaContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaContratoRepository extends JpaRepository<PlantillaContrato, Long> {

    List<PlantillaContrato> findByProveedorId(Long proveedorId);

    List<PlantillaContrato> findByProveedorIdAndEstado(Long proveedorId, EstadoBasico estado);

    Optional<PlantillaContrato> findByProveedorIdAndEsDefaultTrue(Long proveedorId);
}
