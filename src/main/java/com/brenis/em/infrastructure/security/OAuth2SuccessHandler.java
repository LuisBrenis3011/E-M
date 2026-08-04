package com.brenis.em.infrastructure.security;

import com.brenis.em.domain.usuario.Usuario;
import com.brenis.em.domain.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final JwtProvider jwtProvider;
    private final String redirectUri;
    private final String registrationUri;

    public OAuth2SuccessHandler(UsuarioRepository usuarioRepository,
                                JwtProvider jwtProvider,
                                @Value("${app.oauth2.redirect-uri}") String redirectUri,
                                @Value("${app.oauth2.registration-uri}") String registrationUri) {
        this.usuarioRepository = usuarioRepository;
        this.jwtProvider = jwtProvider;
        this.redirectUri = redirectUri;
        this.registrationUri = registrationUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String givenName = oAuth2User.getAttribute("given_name");
        String familyName = oAuth2User.getAttribute("family_name");

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null && usuario.getEstado().name().equals("ACTIVO")) {
            String token = jwtProvider.generateToken(email, usuario.getRol().name());
            Long proveedorId = usuario.getProveedor() != null
                    ? usuario.getProveedor().getId() : null;

            response.sendRedirect(redirectUri
                    + "?token=" + token
                    + "&email=" + encode(email)
                    + "&nombre=" + encode(name != null ? name : "")
                    + "&proveedorId=" + (proveedorId != null ? proveedorId : ""));
        } else {
            response.sendRedirect(registrationUri
                    + "?email=" + encode(email)
                    + "&nombre=" + encode(givenName != null ? givenName : name != null ? name : "")
                    + "&apellido=" + encode(familyName != null ? familyName : ""));
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value != null ? value : "", StandardCharsets.UTF_8);
    }
}
