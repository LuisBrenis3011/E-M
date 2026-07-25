package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.response.ContratoDocumentoResponse;
import com.brenis.em.application.mapper.ContratoDocumentoMapper;
import com.brenis.em.application.service.PdfGenerationService;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@PreAuthorize("hasRole('PROVEEDOR')")
public class DocumentoController {

    private final PdfGenerationService pdfGenerationService;
    private final ContratoDocumentoMapper documentoMapper;

    public DocumentoController(PdfGenerationService pdfGenerationService,
                               ContratoDocumentoMapper documentoMapper) {
        this.pdfGenerationService = pdfGenerationService;
        this.documentoMapper = documentoMapper;
    }

    @PostMapping("/generar/{contratoId}")
    public ResponseEntity<ContratoDocumentoResponse> generar(
            @PathVariable Long contratoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var documento = pdfGenerationService.generarContrato(contratoId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentoMapper.toResponse(documento));
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<ContratoDocumentoResponse>> getByContrato(
            @PathVariable Long contratoId) {
        return ResponseEntity.ok(
                documentoMapper.toResponseList(
                        pdfGenerationService.getDocumentosByContrato(contratoId)));
    }
}
