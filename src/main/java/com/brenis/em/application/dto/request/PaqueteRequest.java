package com.brenis.em.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaqueteRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    private String descripcion;

    @NotNull
    private Long categoriaId;

    private Long tematicaId;

    @NotNull
    @PositiveOrZero
    private BigDecimal precioBase;

    private BigDecimal duracionBaseHoras;

    private List<DetallePaqueteRequest> detalles;
}
