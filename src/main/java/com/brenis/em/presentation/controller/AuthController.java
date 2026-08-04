package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.ChangePasswordRequest;
import com.brenis.em.application.dto.request.LoginRequest;
import com.brenis.em.application.dto.request.ProveedorUpdateRequest;
import com.brenis.em.application.dto.request.RegisterEmpresaRequest;
import com.brenis.em.application.dto.request.RegisterGoogleRequest;
import com.brenis.em.application.dto.request.RegisterRequest;
import com.brenis.em.application.dto.response.EmpresaResponse;
import com.brenis.em.application.dto.response.JwtResponse;
import com.brenis.em.application.dto.response.MeResponse;
import com.brenis.em.application.dto.response.ProveedorResponse;
import com.brenis.em.application.facade.AuthFacade;
import com.brenis.em.application.mapper.ProveedorMapper;
import com.brenis.em.application.service.IProveedorService;
import com.brenis.em.application.service.IUsuarioService;
import com.brenis.em.domain.proveedor.Proveedor;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthFacade authFacade;
    private final IProveedorService proveedorService;
    private final IUsuarioService usuarioService;
    private final ProveedorMapper proveedorMapper;

    public AuthController(AuthFacade authFacade,
                          IProveedorService proveedorService,
                          IUsuarioService usuarioService,
                          ProveedorMapper proveedorMapper) {
        this.authFacade = authFacade;
        this.proveedorService = proveedorService;
        this.usuarioService = usuarioService;
        this.proveedorMapper = proveedorMapper;
    }

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        // Devuelve todos los atributos que Google nos envía (nombre, email, foto, etc.)
        return ResponseEntity.ok(principal.getAttributes());
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authFacade.login(request));
    }

    @PostMapping("/register-empresa")
    public ResponseEntity<EmpresaResponse> registerEmpresa(
            @Valid @RequestBody RegisterEmpresaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authFacade.registerEmpresa(request));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authFacade.register(request));
    }

    @PostMapping("/register-google")
    public ResponseEntity<JwtResponse> registerGoogle(
            @Valid @RequestBody RegisterGoogleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authFacade.registerGoogle(request));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(MeResponse.builder()
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .nombre(userDetails.getNombre())
                .apellido(userDetails.getApellido())
                .rol(userDetails.getRol())
                .proveedorId(userDetails.getProveedorId())
                .build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        usuarioService.changePassword(userDetails.getEmail(),
                request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proveedor")
    public ResponseEntity<ProveedorResponse> getProveedor(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long proveedorId = userDetails.getProveedorId();
        if (proveedorId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                proveedorMapper.toResponse(proveedorService.findById(proveedorId)));
    }

    @PutMapping("/proveedor")
    public ResponseEntity<ProveedorResponse> updateProveedor(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProveedorUpdateRequest request) {
        Long proveedorId = userDetails.getProveedorId();
        if (proveedorId == null) {
            return ResponseEntity.notFound().build();
        }
        Proveedor datos = Proveedor.builder()
                .nombreEmpresa(request.getNombreEmpresa())
                .ruc(request.getRuc())
                .nombreGerente(request.getNombreGerente())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .logoUrl(request.getLogoUrl())
                .terminosCondiciones(request.getTerminosCondiciones())
                .build();
        return ResponseEntity.ok(
                proveedorMapper.toResponse(proveedorService.update(proveedorId, datos)));
    }
}
