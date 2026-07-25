package com.brenis.em.presentation.controller;

import com.brenis.em.application.service.ICategoriaService;
import com.brenis.em.application.service.ITematicaService;
import com.brenis.em.domain.categoria.Categoria;
import com.brenis.em.domain.tematica.Tematica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final ICategoriaService categoriaService;
    private final ITematicaService tematicaService;

    public CategoriaController(ICategoriaService categoriaService,
                               ITematicaService tematicaService) {
        this.categoriaService = categoriaService;
        this.tematicaService = tematicaService;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @GetMapping("/{id}/tematicas")
    public ResponseEntity<List<Tematica>> findTematicas(@PathVariable Long id) {
        return ResponseEntity.ok(tematicaService.findByCategoria(id));
    }
}
