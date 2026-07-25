package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.IUsuarioService;
import com.brenis.em.domain.enums.RolUsuario;
import com.brenis.em.domain.usuario.Usuario;
import com.brenis.em.domain.repository.ProveedorRepository;
import com.brenis.em.domain.repository.UsuarioRepository;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ProveedorRepository proveedorRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              ProveedorRepository proveedorRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.proveedorRepository = proveedorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con email: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public Usuario saveProveedor(Usuario usuario, Long proveedorId) {
        usuario.setRol(RolUsuario.PROVEEDOR);
        usuario.setProveedor(proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", proveedorId)));
        return usuarioRepository.save(usuario);
    }

    @Override
    public void updatePassword(Long id, String newPassword) {
        Usuario usuario = findById(id);
        usuario.setContrasenaHash(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    @Override
    public void updateLastAccess(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }
}
