package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.LoginRequest;
import com.brenis.em.application.dto.request.RegisterRequest;
import com.brenis.em.application.dto.response.JwtResponse;
import com.brenis.em.application.service.ProveedorService;
import com.brenis.em.application.service.UsuarioService;
import com.brenis.em.domain.proveedor.Proveedor;
import com.brenis.em.domain.usuario.Usuario;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {

    private final UsuarioService usuarioService;
    private final ProveedorService proveedorService;

    public AuthFacade(UsuarioService usuarioService, ProveedorService proveedorService) {
        this.usuarioService = usuarioService;
        this.proveedorService = proveedorService;
    }

    public JwtResponse login(LoginRequest request) {
        String token = usuarioService.login(request.getEmail(), request.getPassword());
        Usuario usuario = usuarioService.getByEmail(request.getEmail());

        JwtResponse response = new JwtResponse(
                token, usuario.getEmail(), usuario.getNombre(),
                usuario.getApellido(), usuario.getRol().name(),
                usuario.getProveedor() != null ? usuario.getProveedor().getId() : null);

        return response;
    }

    public JwtResponse register(RegisterRequest request) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombreEmpresa(request.getNombreEmpresa());
        proveedor.setRuc(request.getRuc());
        proveedor.setNombreGerente(request.getNombreGerente());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setTelefono(request.getTelefonoEmpresa());
        proveedor.setEmail(request.getEmail());
        proveedor = proveedorService.saveOrUpdate(proveedor);

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .contrasenaHash(request.getPassword())
                .build();

        usuario = usuarioService.registerProveedor(usuario, proveedor.getId());

        String token = usuarioService.login(request.getEmail(), request.getPassword());

        return new JwtResponse(
                token, usuario.getEmail(), usuario.getNombre(),
                usuario.getApellido(), usuario.getRol().name(),
                usuario.getProveedor().getId());
    }
}
