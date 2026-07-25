package com.brenis.em.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventarioRequest {

    @NotBlank
    @Size(max = 200)
    private String nombre;

    private String descripcion;

    @NotNull
    @PositiveOrZero
    private Integer cantidadDisponible;

    @NotNull
    @PositiveOrZero
    private BigDecimal precioReferencial;
}
