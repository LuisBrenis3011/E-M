package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.PlantillaRequest;
import com.brenis.em.application.dto.response.PlantillaResponse;
import com.brenis.em.application.facade.PlantillaFacade;
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
@RequestMapping("/api/plantillas")
@PreAuthorize("hasRole('PROVEEDOR')")
public class PlantillaController {

    private final PlantillaFacade plantillaFacade;

    public PlantillaController(PlantillaFacade plantillaFacade) {
        this.plantillaFacade = plantillaFacade;
    }

    @PostMapping
    public ResponseEntity<PlantillaResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PlantillaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(plantillaFacade.create(userDetails.getProveedorId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantillaResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody PlantillaRequest request) {
        return ResponseEntity.ok(plantillaFacade.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantillaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(plantillaFacade.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PlantillaResponse>> findAll(
            @AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable) {
        return ResponseEntity.ok(PageUtils.toPage(
                plantillaFacade.findAllByProveedor(userDetails.getProveedorId()), pageable));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        plantillaFacade.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        plantillaFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
