package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long id;
    private String nombreCompleto;
    private String dni;
    private String telefono;
    private String direccion;
    private String referencia;
    private String email;
    private LocalDateTime fechaRegistro;
}
