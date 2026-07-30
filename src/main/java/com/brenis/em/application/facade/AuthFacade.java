package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.LoginRequest;
import com.brenis.em.application.dto.request.RegisterEmpresaRequest;
import com.brenis.em.application.dto.request.RegisterRequest;
import com.brenis.em.application.dto.response.EmpresaResponse;
import com.brenis.em.application.dto.response.JwtResponse;
import com.brenis.em.application.service.IAuthService;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {

    private final IAuthService authService;

    public AuthFacade(IAuthService authService) {
        this.authService = authService;
    }

    public JwtResponse login(LoginRequest request) {
        return authService.login(request);
    }

    public JwtResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    public EmpresaResponse registerEmpresa(RegisterEmpresaRequest request) {
        return authService.registerEmpresa(request);
    }
}
