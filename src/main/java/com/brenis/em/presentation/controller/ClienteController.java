package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.ClienteRequest;
import com.brenis.em.application.dto.response.ClienteResponse;
import com.brenis.em.application.facade.ClienteFacade;
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
    public ResponseEntity<ClienteResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteFacade.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteFacade.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> findAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String dni,
            Pageable pageable) {
        var all = dni != null && !dni.isBlank()
                ? clienteFacade.searchByDni(userDetails.getProveedorId(), dni)
                : q != null && !q.isBlank()
                        ? clienteFacade.search(userDetails.getProveedorId(), q)
                        : clienteFacade.findAllByProveedor(userDetails.getProveedorId());
        return ResponseEntity.ok(PageUtils.toPage(all, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
