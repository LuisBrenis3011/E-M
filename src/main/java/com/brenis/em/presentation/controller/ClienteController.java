package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.ClienteRequest;
import com.brenis.em.application.dto.response.ClienteResponse;
import com.brenis.em.application.facade.ClienteFacade;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@PreAuthorize("hasRole('PROVEEDOR')")
public class ClienteController {

    private final ClienteFacade clienteFacade;

    public ClienteController(ClienteFacade clienteFacade) {
        this.clienteFacade = clienteFacade;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteFacade.create(userDetails.getProveedorId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteFacade.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteFacade.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String q) {
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(clienteFacade.search(userDetails.getProveedorId(), q));
        }
        return ResponseEntity.ok(clienteFacade.getAllByProveedor(userDetails.getProveedorId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
