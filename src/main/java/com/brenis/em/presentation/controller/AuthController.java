package com.brenis.em.presentation.controller;

import com.brenis.em.application.dto.request.LoginRequest;
import com.brenis.em.application.dto.request.RegisterRequest;
import com.brenis.em.application.dto.response.JwtResponse;
import com.brenis.em.application.dto.response.ProveedorResponse;
import com.brenis.em.application.facade.AuthFacade;
import com.brenis.em.application.mapper.ProveedorMapper;
import com.brenis.em.application.service.ProveedorService;
import com.brenis.em.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthFacade authFacade;
    private final ProveedorService proveedorService;
    private final ProveedorMapper proveedorMapper;

    public AuthController(AuthFacade authFacade,
                          ProveedorService proveedorService,
                          ProveedorMapper proveedorMapper) {
        this.authFacade = authFacade;
        this.proveedorService = proveedorService;
        this.proveedorMapper = proveedorMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authFacade.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authFacade.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userDetails);
    }

    @GetMapping("/proveedor")
    public ResponseEntity<ProveedorResponse> getProveedor(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long proveedorId = userDetails.getProveedorId();
        if (proveedorId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                proveedorMapper.toResponse(proveedorService.getById(proveedorId)));
    }
}
