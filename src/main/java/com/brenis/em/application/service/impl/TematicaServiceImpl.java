package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.ITematicaService;
import com.brenis.em.domain.tematica.Tematica;
import com.brenis.em.domain.repository.CategoriaRepository;
import com.brenis.em.domain.repository.TematicaRepository;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TematicaServiceImpl implements ITematicaService {

    private final TematicaRepository tematicaRepository;
    private final CategoriaRepository categoriaRepository;

    public TematicaServiceImpl(TematicaRepository tematicaRepository,
                               CategoriaRepository categoriaRepository) {
        this.tematicaRepository = tematicaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Tematica> findAll() {
        return tematicaRepository.findAll();
    }

    @Override
    public List<Tematica> findByCategoria(Long categoriaId) {
        return tematicaRepository.findByCategoriaId(categoriaId);
    }

    @Override
    public Tematica findById(Long id) {
        return tematicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tematica", id));
    }

    @Override
    public Tematica save(Long categoriaId, Tematica tematica) {
        tematica.setCategoria(categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", categoriaId)));
        return tematicaRepository.save(tematica);
    }

    @Override
    public Tematica update(Long id, Long categoriaId, Tematica datos) {
        Tematica existente = findById(id);
        existente.setNombre(datos.getNombre());
        existente.setImagenReferencial(datos.getImagenReferencial());
        existente.setCategoria(categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", categoriaId)));
        return tematicaRepository.save(existente);
    }

    @Override
    public void deleteById(Long id) {
        if (!tematicaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tematica", id);
        }
        tematicaRepository.deleteById(id);
    }
}
