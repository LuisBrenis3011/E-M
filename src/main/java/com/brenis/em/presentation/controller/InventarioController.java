package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.InventarioRequest;
import com.brenis.em.application.dto.response.InventarioResponse;
import com.brenis.em.application.facade.InventarioFacade;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@PreAuthorize("hasRole('PROVEEDOR')")
public class InventarioController {

    private final InventarioFacade inventarioFacade;

    public InventarioController(InventarioFacade inventarioFacade) {
        this.inventarioFacade = inventarioFacade;
    }

    @PostMapping
    public ResponseEntity<InventarioResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioFacade.create(userDetails.getProveedorId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioFacade.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioFacade.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<InventarioResponse>> findAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        List<InventarioResponse> all = inventarioFacade.findAllByProveedor(
                userDetails.getProveedorId());
        return ResponseEntity.ok(toPage(all, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventarioResponse>> search(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String q) {
        return ResponseEntity.ok(inventarioFacade.search(userDetails.getProveedorId(), q));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        inventarioFacade.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventarioFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private <T> Page<T> toPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        if (start > list.size()) return new PageImpl<>(List.of(), pageable, list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
