package com.brenis.em.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequest {

    @NotBlank
    @Size(max = 200)
    private String nombreCompleto;

    @NotBlank
    @Size(max = 20)
    private String dni;

    @NotBlank
    @Size(max = 20)
    private String telefono;

    @Size(max = 255)
    private String direccion;

    @Size(max = 255)
    private String referencia;

    @Size(max = 150)
    private String email;
}
