package com.brenis.em.presentation.controller;

import com.brenis.em.domain.tematica.Tematica;
import com.brenis.em.application.service.TematicaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tematicas")
public class TematicaController {

    private final TematicaService tematicaService;

    public TematicaController(TematicaService tematicaService) {
        this.tematicaService = tematicaService;
    }

    @GetMapping
    public ResponseEntity<List<Tematica>> getAll(
            @RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ResponseEntity.ok(tematicaService.getByCategoria(categoriaId));
        }
        return ResponseEntity.ok(tematicaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tematica> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tematicaService.getById(id));
    }
}
