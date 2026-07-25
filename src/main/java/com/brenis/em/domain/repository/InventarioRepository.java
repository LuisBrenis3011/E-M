package com.brenis.em.domain.repository;

import com.brenis.em.domain.enums.EstadoBasico;
import com.brenis.em.domain.inventario.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    List<Inventario> findByProveedorId(Long proveedorId);

    List<Inventario> findByProveedorIdAndEstado(Long proveedorId, EstadoBasico estado);

    @Query("SELECT i FROM Inventario i WHERE i.proveedor.id = :proveedorId " +
           "AND i.estado = 'ACTIVO' AND LOWER(i.nombre) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY i.nombre")
    List<Inventario> searchInventario(@Param("proveedorId") Long proveedorId, @Param("q") String q);
}
