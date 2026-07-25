package com.brenis.em.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContratoRequest {

    @NotNull
    private Long eventoId;

    @NotNull
    private Long paqueteId;

    @NotNull
    @PositiveOrZero
    private BigDecimal costoMovilidad;

    @NotNull
    @PositiveOrZero
    private BigDecimal montoAdelanto;

    private String duracion;

    private String observaciones;
}
