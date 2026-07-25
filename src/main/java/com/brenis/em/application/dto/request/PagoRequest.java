package com.brenis.em.application.dto.request;

import com.brenis.em.domain.enums.TipoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PagoRequest {

    @NotNull
    private Long contratoId;

    @NotNull
    private TipoPago tipoPago;

    @NotNull
    @Positive
    private java.math.BigDecimal monto;

    @NotNull
    private com.brenis.em.domain.enums.MetodoPago metodoPago;

    private String codigoOperacion;

    private String notas;
}
