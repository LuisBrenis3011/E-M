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
public class PlantillaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private String contenidoHtml;
    private String placeholders;
    private Boolean esDefault;
    private String estado;
    private LocalDateTime fechaCreacion;
}
