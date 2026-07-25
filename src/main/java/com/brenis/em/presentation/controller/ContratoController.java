package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.ContratoRequest;
import com.brenis.em.application.dto.request.DetalleContratoRequest;
import com.brenis.em.application.dto.response.ContratoResponse;
import com.brenis.em.application.facade.ContratoFacade;
import com.brenis.em.domain.enums.EstadoContrato;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ContratoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contratoFacade.getById(id));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<ContratoResponse> getByEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(contratoFacade.getByEvento(eventoId));
    }

    @GetMapping
    public ResponseEntity<List<ContratoResponse>> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(contratoFacade.getAllByProveedor(userDetails.getProveedorId()));
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
    public ResponseEntity<ContratoResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoContrato estado) {
        return ResponseEntity.ok(contratoFacade.cambiarEstado(id, estado));
    }
}
