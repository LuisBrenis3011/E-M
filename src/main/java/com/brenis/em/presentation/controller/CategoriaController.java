package com.brenis.em.presentation.controller;

import com.brenis.em.domain.categoria.Categoria;
import com.brenis.em.domain.tematica.Tematica;
import com.brenis.em.application.service.CategoriaService;
import com.brenis.em.application.service.TematicaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final TematicaService tematicaService;

    public CategoriaController(CategoriaService categoriaService,
                               TematicaService tematicaService) {
        this.categoriaService = categoriaService;
        this.tematicaService = tematicaService;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> getAll() {
        return ResponseEntity.ok(categoriaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.getById(id));
    }

    @GetMapping("/{id}/tematicas")
    public ResponseEntity<List<Tematica>> getTematicas(@PathVariable Long id) {
        return ResponseEntity.ok(tematicaService.getByCategoria(id));
    }
}
