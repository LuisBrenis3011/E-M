package com.brenis.em.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorUpdateRequest {

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

    @Size(max = 150)
    private String email;

    @Size(max = 255)
    private String logoUrl;

    private String terminosCondiciones;
}
