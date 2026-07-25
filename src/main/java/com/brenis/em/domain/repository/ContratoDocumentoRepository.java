package com.brenis.em.domain.repository;

import com.brenis.em.domain.documento.ContratoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoDocumentoRepository extends JpaRepository<ContratoDocumento, Long> {

    List<ContratoDocumento> findByContratoIdOrderByVersionDesc(Long contratoId);

    Optional<ContratoDocumento> findTopByContratoIdOrderByVersionDesc(Long contratoId);
}
