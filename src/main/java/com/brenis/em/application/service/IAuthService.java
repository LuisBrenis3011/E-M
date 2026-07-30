package com.brenis.em.application.service;

import com.brenis.em.application.dto.request.LoginRequest;
import com.brenis.em.application.dto.request.RegisterEmpresaRequest;
import com.brenis.em.application.dto.request.RegisterRequest;
import com.brenis.em.application.dto.response.EmpresaResponse;
import com.brenis.em.application.dto.response.JwtResponse;

public interface IAuthService {

    JwtResponse login(LoginRequest request);

    JwtResponse register(RegisterRequest request);

    EmpresaResponse registerEmpresa(RegisterEmpresaRequest request);
}
