package com.brenis.em.application.service;

import com.brenis.em.domain.documento.ContratoDocumento;

import java.util.List;

public interface IPdfGenerationService {

    ContratoDocumento generarContrato(Long contratoId, Long generadoPor);

    ContratoDocumento findById(Long id);

    List<ContratoDocumento> findByContrato(Long contratoId);
}
