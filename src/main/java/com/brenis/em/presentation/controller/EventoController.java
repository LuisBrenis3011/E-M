package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.EventoRequest;
import com.brenis.em.application.dto.response.EventoResponse;
import com.brenis.em.application.facade.EventoFacade;
import com.brenis.em.domain.enums.EstadoEvento;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@PreAuthorize("hasRole('PROVEEDOR')")
public class EventoController {

    private final EventoFacade eventoFacade;

    public EventoController(EventoFacade eventoFacade) {
        this.eventoFacade = eventoFacade;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> create(@Valid @RequestBody EventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoFacade.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody EventoRequest request) {
        return ResponseEntity.ok(eventoFacade.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventoFacade.getById(id));
    }

    @GetMapping("/calendario")
    public ResponseEntity<List<EventoResponse>> getCalendario(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(eventoFacade.getCalendario(
                userDetails.getProveedorId(), inicio, fin));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EventoResponse>> getByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(eventoFacade.getByCliente(clienteId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EventoResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoEvento estado) {
        return ResponseEntity.ok(eventoFacade.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventoFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
