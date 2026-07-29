package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.response.ContratoDocumentoResponse;
import com.brenis.em.application.mapper.ContratoDocumentoMapper;
import com.brenis.em.application.service.IPdfGenerationService;
import com.brenis.em.domain.documento.ContratoDocumento;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import com.brenis.em.infrastructure.storage.FileStorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@PreAuthorize("hasRole('PROVEEDOR')")
public class DocumentoController {

    private final IPdfGenerationService pdfGenerationService;
    private final ContratoDocumentoMapper documentoMapper;
    private final FileStorageService fileStorageService;

    public DocumentoController(IPdfGenerationService pdfGenerationService,
                               ContratoDocumentoMapper documentoMapper,
                               FileStorageService fileStorageService) {
        this.pdfGenerationService = pdfGenerationService;
        this.documentoMapper = documentoMapper;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/generar/{contratoId}")
    public ResponseEntity<ContratoDocumentoResponse> generar(
            @PathVariable Long contratoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var documento = pdfGenerationService.generarContrato(contratoId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentoMapper.toResponse(documento));
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<byte[]> descargar(@PathVariable Long id) {
        ContratoDocumento doc = pdfGenerationService.findById(id);
        byte[] pdf = fileStorageService.readFile(doc.getUrlPdf());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=contrato_" + doc.getContrato().getId()
                                + "_v" + doc.getVersion() + ".pdf")
                .body(pdf);
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<ContratoDocumentoResponse>> findByContrato(
            @PathVariable Long contratoId) {
        return ResponseEntity.ok(
                documentoMapper.toResponseList(
                        pdfGenerationService.findByContrato(contratoId)));
    }
}
