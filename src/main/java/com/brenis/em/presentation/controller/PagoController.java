package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.response.PagoResponse;
import com.brenis.em.application.facade.PagoFacade;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@PreAuthorize("hasRole('PROVEEDOR')")
public class PagoController {

    private final PagoFacade pagoFacade;

    public PagoController(PagoFacade pagoFacade) {
        this.pagoFacade = pagoFacade;
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

        com.brenis.em.application.dto.request.PagoRequest request =
                new com.brenis.em.application.dto.request.PagoRequest();
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
    public ResponseEntity<PagoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pagoFacade.getById(id));
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<PagoResponse>> getByContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(pagoFacade.getByContrato(contratoId));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<PagoResponse>> getPendientes() {
        return ResponseEntity.ok(pagoFacade.getPendientes());
    }

    @PatchMapping("/{id}/verificar")
    public ResponseEntity<PagoResponse> verificar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(pagoFacade.verificar(id, userDetails.getId()));
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<PagoResponse> rechazar(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(pagoFacade.rechazar(id, motivo));
    }
}
