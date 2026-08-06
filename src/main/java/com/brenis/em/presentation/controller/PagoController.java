package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.response.PagoResponse;
import com.brenis.em.application.facade.PagoFacade;
import com.brenis.em.application.service.IPagoService;
import com.brenis.em.domain.pago.Pago;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import com.brenis.em.infrastructure.storage.FileStorageService;
import com.brenis.em.infrastructure.util.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@PreAuthorize("hasRole('PROVEEDOR')")
public class PagoController {

    private final PagoFacade pagoFacade;
    private final IPagoService pagoService;
    private final FileStorageService fileStorageService;

    public PagoController(PagoFacade pagoFacade,
                          IPagoService pagoService,
                          FileStorageService fileStorageService) {
        this.pagoFacade = pagoFacade;
        this.pagoService = pagoService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<PagoResponse> create(
            @RequestParam("contratoId") Long contratoId,
            @RequestParam("tipoPago") String tipoPago,
            @RequestParam("monto") java.math.BigDecimal monto,
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam(value = "codigoOperacion", required = false) String codigoOperacion,
            @RequestParam(value = "notas", required = false) String notas,
            @RequestParam(value = "comprobante", required = false) MultipartFile comprobante) {

        var request = new com.brenis.em.application.dto.request.PagoRequest();
        request.setContratoId(contratoId);
        request.setTipoPago(com.brenis.em.domain.enums.TipoPago.valueOf(tipoPago));
        request.setMonto(monto);
        request.setMetodoPago(com.brenis.em.domain.enums.MetodoPago.valueOf(metodoPago));
        request.setCodigoOperacion(codigoOperacion);
        request.setNotas(notas);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pagoFacade.create(request, comprobante));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pagoFacade.findById(id));
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<Page<PagoResponse>> findByContrato(
            @PathVariable Long contratoId, Pageable pageable) {
        return ResponseEntity.ok(PageUtils.toPage(
                pagoFacade.findByContrato(contratoId), pageable));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<Page<PagoResponse>> findPendientes(Pageable pageable) {
        return ResponseEntity.ok(PageUtils.toPage(
                pagoFacade.findPendientes(), pageable));
    }

    @PatchMapping("/{id}/verificar")
    public ResponseEntity<PagoResponse> verificar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(pagoFacade.verificar(id, userDetails.getId()));
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<PagoResponse> rechazar(@PathVariable Long id,
                                                  @RequestParam String motivo) {
        return ResponseEntity.ok(pagoFacade.rechazar(id, motivo));
    }

    @GetMapping("/comprobante/{id}")
    public ResponseEntity<byte[]> verComprobante(@PathVariable Long id) {
        Pago pago = pagoService.findById(id);
        if (pago.getUrlComprobante() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] image = fileStorageService.readFile(pago.getUrlComprobante());
        String nombre = pago.getNombreArchivo();
        MediaType mediaType = MediaType.IMAGE_JPEG;
        if (nombre != null) {
            String lower = nombre.toLowerCase();
            if (lower.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
            else if (lower.endsWith(".gif")) mediaType = MediaType.IMAGE_GIF;
            else if (lower.endsWith(".pdf")) mediaType = MediaType.APPLICATION_PDF;
        }
        return ResponseEntity.ok().contentType(mediaType).body(image);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pagoFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
