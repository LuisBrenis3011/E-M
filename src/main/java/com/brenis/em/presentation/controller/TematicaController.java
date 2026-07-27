package com.brenis.em.presentation.controller;

import com.brenis.em.application.service.ITematicaService;
import com.brenis.em.domain.tematica.Tematica;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tematicas")
public class TematicaController {

    private final ITematicaService tematicaService;

    public TematicaController(ITematicaService tematicaService) {
        this.tematicaService = tematicaService;
    }

    @GetMapping
    public ResponseEntity<List<Tematica>> findAll(
            @RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ResponseEntity.ok(tematicaService.findByCategoria(categoriaId));
        }
        return ResponseEntity.ok(tematicaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tematica> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tematicaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Tematica> create(@Valid @RequestBody Tematica tematica,
                                            @RequestParam Long categoriaId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tematicaService.save(categoriaId, tematica));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tematica> update(@PathVariable Long id,
                                            @Valid @RequestBody Tematica tematica,
                                            @RequestParam Long categoriaId) {
        return ResponseEntity.ok(tematicaService.update(id, categoriaId, tematica));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tematicaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
