package com.brenis.em.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetallePaqueteRequest {

    @NotNull
    private Long inventarioId;

    @NotNull
    @Positive
    private Integer cantidadIncluida;

    @NotNull
    private BigDecimal precioUnitario;

    private Boolean esObsequio = false;

    private Integer orden = 0;
}
