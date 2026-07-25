package com.brenis.em.domain.repository;

import com.brenis.em.domain.paquete.DetallePaquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePaqueteRepository extends JpaRepository<DetallePaquete, Long> {

    List<DetallePaquete> findByPaqueteIdOrderByOrden(Long paqueteId);

    void deleteByPaqueteId(Long paqueteId);
}
