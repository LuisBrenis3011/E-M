package com.brenis.em.application.service;

import com.brenis.em.domain.usuario.Usuario;

public interface IUsuarioService {

    Usuario findById(Long id);

    Usuario findByEmail(String email);

    boolean existsByEmail(String email);

    Usuario saveProveedor(Usuario usuario, Long proveedorId);

    void updatePassword(Long id, String newPassword);

    void changePassword(String email, String oldPassword, String newPassword);

    void updateLastAccess(String email);
}
