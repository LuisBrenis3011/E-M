package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String email;
    private String nombre;
    private String apellido;
    private String rol;
    private Long proveedorId;
}
