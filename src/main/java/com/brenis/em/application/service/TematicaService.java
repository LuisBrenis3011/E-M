package com.brenis.em.application.service;

import com.brenis.em.domain.tematica.Tematica;
import com.brenis.em.domain.repository.CategoriaRepository;
import com.brenis.em.domain.repository.TematicaRepository;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TematicaService {

    private final TematicaRepository tematicaRepository;
    private final CategoriaRepository categoriaRepository;

    public TematicaService(TematicaRepository tematicaRepository,
                           CategoriaRepository categoriaRepository) {
        this.tematicaRepository = tematicaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Tematica> getAll() {
        return tematicaRepository.findAll();
    }

    public List<Tematica> getByCategoria(Long categoriaId) {
        return tematicaRepository.findByCategoriaId(categoriaId);
    }

    public Tematica getById(Long id) {
        return tematicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tematica", id));
    }

    public Tematica save(Long categoriaId, Tematica tematica) {
        tematica.setCategoria(categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", categoriaId)));
        return tematicaRepository.save(tematica);
    }
}
