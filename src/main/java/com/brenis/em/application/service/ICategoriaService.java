package com.brenis.em.application.service;

import com.brenis.em.domain.categoria.Categoria;

import java.util.List;

public interface ICategoriaService {

    List<Categoria> findAll();

    Categoria findById(Long id);

    Categoria save(Categoria categoria);

    Categoria update(Long id, Categoria datos);

    void deleteById(Long id);
}
