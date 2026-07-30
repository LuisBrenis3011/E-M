package com.brenis.em.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterEmpresaRequest {

    @NotBlank
    @Size(max = 150)
    private String nombreEmpresa;

    @NotBlank
    @Size(max = 20)
    private String ruc;

    @NotBlank
    @Size(max = 150)
    private String nombreGerente;

    @Size(max = 255)
    private String direccion;

    @NotBlank
    @Size(max = 20)
    private String telefono;

    @NotBlank
    @Size(max = 100)
    private String adminNombre;

    @NotBlank
    @Size(max = 100)
    private String adminApellido;

    @NotBlank
    @Email
    @Size(max = 150)
    private String adminEmail;

    @NotBlank
    @Size(min = 6, max = 100)
    private String adminPassword;
}
