package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long totalClientes;
    private long totalInventarioActivo;
    private long totalPaquetesActivos;
    private long totalEventosProgramados;
    private long totalContratos;
    private Map<String, Long> contratosPorEstado;
    private BigDecimal ingresosTotales;
    private long pagosPendientes;
    private BigDecimal montoPendienteCobrar;
    private List<EventoResponse> proximosEventos;
    private List<EventoResponse> eventosDelMes;
}
