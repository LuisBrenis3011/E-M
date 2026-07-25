package com.brenis.em.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleContratoRequest {

    @NotNull
    private Long inventarioId;

    @NotNull
    @Positive
    private Integer cantidad;

    @NotNull
    private BigDecimal precioUnitario;

    private Boolean esObsequio = false;

    private String tipoDetalle = "INCLUYE";

    private Integer orden = 0;
}
