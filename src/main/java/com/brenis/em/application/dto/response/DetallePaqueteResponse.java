package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePaqueteResponse {

    private Long id;
    private Long inventarioId;
    private String inventarioNombre;
    private Integer cantidadIncluida;
    private BigDecimal precioUnitario;
    private Boolean esObsequio;
    private Integer orden;
}
