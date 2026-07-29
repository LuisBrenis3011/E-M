package com.brenis.em.infrastructure.security;

import com.brenis.em.domain.enums.EstadoBasico;
import com.brenis.em.domain.usuario.Usuario;
import com.brenis.em.infrastructure.security.constant.SecurityRoles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    @JsonIgnore
    private final String password;
    private final String nombre;
    private final String apellido;
    private final String rol;
    private final Long proveedorId;
    private final boolean activo;

    public static CustomUserDetails fromUsuario(Usuario usuario) {
        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getContrasenaHash(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getRol().name(),
                usuario.getProveedor() != null ? usuario.getProveedor().getId() : null,
                usuario.getEstado() == EstadoBasico.ACTIVO
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(SecurityRoles.ROLE_PREFIX + rol));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
