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
public class EventoResponse {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Long categoriaId;
    private String categoriaNombre;
    private Long tematicaId;
    private String tematicaNombre;
    private Long paqueteId;
    private String paqueteNombre;
    private java.math.BigDecimal paquetePrecio;
    private String tipoEvento;
    private String nombreCumpleanero;
    private Integer edadCumpleanero;
    private LocalDate fechaEvento;
    private LocalTime horaInicio;
    private LocalTime horaFinEstimada;
    private String direccion;
    private String referencia;
    private Integer aforoEstimado;
    private String colorCalendario;
    private String notasInternas;
    private String estado;
}
