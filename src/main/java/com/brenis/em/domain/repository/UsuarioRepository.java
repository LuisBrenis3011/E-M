package com.brenis.em.domain.repository;

import com.brenis.em.domain.enums.RolUsuario;
import com.brenis.em.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Usuario> findByProveedorIdAndRol(Long proveedorId, RolUsuario rol);
}
