package com.brenis.em.domain.repository;

import com.brenis.em.domain.tematica.Tematica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TematicaRepository extends JpaRepository<Tematica, Long> {

    List<Tematica> findByCategoriaId(Long categoriaId);
}
