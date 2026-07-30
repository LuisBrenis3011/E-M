package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContratoResponse {

    private Long id;
    private Long eventoId;
    private String eventoTipo;
    private String estado;
    private BigDecimal montoTotal;
    private BigDecimal costoMovilidad;
    private BigDecimal montoAdelanto;
    private BigDecimal montoPendiente;
    private String duracion;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private List<DetalleContratoResponse> detalles;
}
