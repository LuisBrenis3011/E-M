package com.brenis.em.application.service;

import com.brenis.em.domain.enums.RolUsuario;
import com.brenis.em.domain.usuario.Usuario;
import com.brenis.em.domain.repository.ProveedorRepository;
import com.brenis.em.domain.repository.UsuarioRepository;
import com.brenis.em.infrastructure.exception.BusinessException;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import com.brenis.em.infrastructure.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ProveedorRepository proveedorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          ProveedorRepository proveedorRepository,
                          PasswordEncoder passwordEncoder,
                          JwtProvider jwtProvider,
                          AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.proveedorRepository = proveedorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    public String login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return jwtProvider.generateToken(usuario.getEmail(), usuario.getRol().name());
    }

    public Usuario registerProveedor(Usuario usuario, Long proveedorId) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("El email ya esta registrado");
        }

        usuario.setContrasenaHash(passwordEncoder.encode(usuario.getContrasenaHash()));
        usuario.setRol(RolUsuario.PROVEEDOR);
        usuario.setProveedor(proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", proveedorId)));

        return usuarioRepository.save(usuario);
    }

    public Usuario getById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    public Usuario getByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    public Usuario updatePassword(Long id, String newPassword) {
        Usuario usuario = getById(id);
        usuario.setContrasenaHash(passwordEncoder.encode(newPassword));
        return usuarioRepository.save(usuario);
    }
}
