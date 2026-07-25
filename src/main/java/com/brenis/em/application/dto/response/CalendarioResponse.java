package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarioResponse {

    private Long id;
    private LocalDate fechaEvento;
    private LocalTime horaInicio;
    private LocalTime horaFinEstimada;
    private String tipoEvento;
    private String estado;
    private String colorCalendario;
    private String clienteNombre;
    private String tematicaNombre;
    private java.math.BigDecimal montoTotal;
    private String estadoContrato;
}
