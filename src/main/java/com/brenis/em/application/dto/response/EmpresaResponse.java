package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponse {

    private Long proveedorId;
    private String nombreEmpresa;
    private String ruc;
    private String token;
    private String adminEmail;
}
