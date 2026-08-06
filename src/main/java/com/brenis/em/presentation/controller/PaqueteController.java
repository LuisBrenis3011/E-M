package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.PaqueteRequest;
import com.brenis.em.application.dto.response.PaqueteResponse;
import com.brenis.em.application.facade.PaqueteFacade;
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
    public ResponseEntity<PaqueteResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody PaqueteRequest request) {
        return ResponseEntity.ok(paqueteFacade.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaqueteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paqueteFacade.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PaqueteResponse>> findAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long categoriaId,
            Pageable pageable) {
        var all = categoriaId != null
                ? paqueteFacade.findByCategoria(categoriaId)
                : paqueteFacade.findAllByProveedor(userDetails.getProveedorId());
        return ResponseEntity.ok(PageUtils.toPage(all, pageable));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        paqueteFacade.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paqueteFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
