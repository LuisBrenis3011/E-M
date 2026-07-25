package com.brenis.em.application.service;

import com.brenis.em.domain.tematica.Tematica;

import java.util.List;

public interface ITematicaService {

    List<Tematica> findAll();

    List<Tematica> findByCategoria(Long categoriaId);

    Tematica findById(Long id);

    Tematica save(Long categoriaId, Tematica tematica);
}
