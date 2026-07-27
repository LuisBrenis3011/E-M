package com.brenis.em.presentation.controller;

import com.brenis.em.application.service.ICategoriaService;
import com.brenis.em.application.service.ITematicaService;
import com.brenis.em.domain.categoria.Categoria;
import com.brenis.em.domain.tematica.Tematica;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<Categoria> create(@Valid @RequestBody Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.save(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable Long id,
                                             @Valid @RequestBody Categoria categoria) {
        return ResponseEntity.ok(categoriaService.update(id, categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tematicas")
    public ResponseEntity<List<Tematica>> findTematicas(@PathVariable Long id) {
        return ResponseEntity.ok(tematicaService.findByCategoria(id));
    }
}
