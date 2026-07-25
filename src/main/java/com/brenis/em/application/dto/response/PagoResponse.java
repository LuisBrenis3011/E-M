package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {

    private Long id;
    private Long contratoId;
    private String tipoPago;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private String urlComprobante;
    private String nombreArchivo;
    private String codigoOperacion;
    private String notas;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaVerificacion;
}
