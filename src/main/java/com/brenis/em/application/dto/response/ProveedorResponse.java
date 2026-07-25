package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponse {

    private Long id;
    private String nombreEmpresa;
    private String ruc;
    private String nombreGerente;
    private String direccion;
    private String telefono;
    private String email;
    private String logoUrl;
    private String terminosCondiciones;
}
