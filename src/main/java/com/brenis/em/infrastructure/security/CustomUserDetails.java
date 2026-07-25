package com.brenis.em.infrastructure.security;

import com.brenis.em.domain.usuario.Usuario;
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
    private final String password;
    private final String nombre;
    private final String apellido;
    private final String rol;
    private final Long proveedorId;

    public static CustomUserDetails fromUsuario(Usuario usuario) {
        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getContrasenaHash(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getRol().name(),
                usuario.getProveedor() != null ? usuario.getProveedor().getId() : null
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol));
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
        return true;
    }
}
