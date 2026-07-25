package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.PaqueteRequest;
import com.brenis.em.application.dto.response.PaqueteResponse;
import com.brenis.em.application.facade.PaqueteFacade;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes")
@PreAuthorize("hasRole('PROVEEDOR')")
public class PaqueteController {

    private final PaqueteFacade paqueteFacade;

    public PaqueteController(PaqueteFacade paqueteFacade) {
        this.paqueteFacade = paqueteFacade;
    }

    @PostMapping
    public ResponseEntity<PaqueteResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaqueteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paqueteFacade.create(userDetails.getProveedorId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaqueteResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PaqueteRequest request) {
        return ResponseEntity.ok(paqueteFacade.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaqueteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paqueteFacade.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<PaqueteResponse>> findAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ResponseEntity.ok(paqueteFacade.findByCategoria(categoriaId));
        }
        return ResponseEntity.ok(paqueteFacade.findAllByProveedor(userDetails.getProveedorId()));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        paqueteFacade.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
