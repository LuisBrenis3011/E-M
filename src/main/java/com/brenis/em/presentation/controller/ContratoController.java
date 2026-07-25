package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.ContratoRequest;
import com.brenis.em.application.dto.request.DetalleContratoRequest;
import com.brenis.em.application.dto.response.ContratoResponse;
import com.brenis.em.application.facade.ContratoFacade;
import com.brenis.em.domain.enums.EstadoContrato;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import com.brenis.em.infrastructure.util.PageUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contratos")
@PreAuthorize("hasRole('PROVEEDOR')")
public class ContratoController {

    private final ContratoFacade contratoFacade;

    public ContratoController(ContratoFacade contratoFacade) {
        this.contratoFacade = contratoFacade;
    }

    @PostMapping
    public ResponseEntity<ContratoResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ContratoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contratoFacade.createFromPaquete(userDetails.getProveedorId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contratoFacade.findById(id));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<ContratoResponse> findByEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(contratoFacade.findByEvento(eventoId));
    }

    @GetMapping
    public ResponseEntity<Page<ContratoResponse>> findAll(
            @AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable) {
        return ResponseEntity.ok(PageUtils.toPage(
                contratoFacade.findAllByProveedor(userDetails.getProveedorId()), pageable));
    }

    @PostMapping("/{contratoId}/detalles")
    public ResponseEntity<ContratoResponse> addDetalle(
            @PathVariable Long contratoId,
            @Valid @RequestBody DetalleContratoRequest request) {
        return ResponseEntity.ok(contratoFacade.addDetalle(contratoId, request));
    }

    @DeleteMapping("/detalles/{detalleId}")
    public ResponseEntity<Void> removeDetalle(@PathVariable Long detalleId) {
        contratoFacade.removeDetalle(detalleId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ContratoResponse> cambiarEstado(@PathVariable Long id,
                                                           @RequestParam EstadoContrato estado) {
        return ResponseEntity.ok(contratoFacade.cambiarEstado(id, estado));
    }
}
