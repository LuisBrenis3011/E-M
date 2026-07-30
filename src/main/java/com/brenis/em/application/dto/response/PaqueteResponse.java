package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
    private BigDecimal duracionBaseHoras;
    private String estado;
    private Long categoriaId;
    private String categoriaNombre;
    private List<DetallePaqueteResponse> detalles;
}
