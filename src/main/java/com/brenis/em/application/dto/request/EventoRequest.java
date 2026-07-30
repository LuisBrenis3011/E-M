package com.brenis.em.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventoRequest {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long categoriaId;

    private Long tematicaId;

    @NotNull
    private Long paqueteId;

    @Size(max = 100)
    private String tipoEvento;

    @Size(max = 150)
    private String nombreCumpleanero;

    private Integer edadCumpleanero;

    @NotNull
    private LocalDate fechaEvento;

    @NotNull
    private LocalTime horaInicio;

    private LocalTime horaFinEstimada;

    @NotBlank
    @Size(max = 255)
    private String direccion;

    @Size(max = 255)
    private String referencia;

    private Integer aforoEstimado;

    private String colorCalendario;

    private String notasInternas;
}
