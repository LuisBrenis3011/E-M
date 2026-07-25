package com.brenis.em.application.service.impl;

import com.brenis.em.application.dto.response.DashboardResponse;
import com.brenis.em.application.mapper.EventoMapper;
import com.brenis.em.application.service.IDashboardService;
import com.brenis.em.domain.enums.EstadoBasico;
import com.brenis.em.domain.enums.EstadoContrato;
import com.brenis.em.domain.enums.EstadoEvento;
import com.brenis.em.domain.enums.EstadoPago;
import com.brenis.em.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements IDashboardService {

    private final ClienteRepository clienteRepository;
    private final InventarioRepository inventarioRepository;
    private final PaqueteRepository paqueteRepository;
    private final EventoRepository eventoRepository;
    private final ContratoRepository contratoRepository;
    private final PagoRepository pagoRepository;
    private final EventoMapper eventoMapper;

    public DashboardServiceImpl(ClienteRepository clienteRepository,
                                InventarioRepository inventarioRepository,
                                PaqueteRepository paqueteRepository,
                                EventoRepository eventoRepository,
                                ContratoRepository contratoRepository,
                                PagoRepository pagoRepository,
                                EventoMapper eventoMapper) {
        this.clienteRepository = clienteRepository;
        this.inventarioRepository = inventarioRepository;
        this.paqueteRepository = paqueteRepository;
        this.eventoRepository = eventoRepository;
        this.contratoRepository = contratoRepository;
        this.pagoRepository = pagoRepository;
        this.eventoMapper = eventoMapper;
    }

    @Override
    public DashboardResponse getResumen(Long proveedorId) {
        long totalClientes = clienteRepository.countByProveedorId(proveedorId);
        long totalInventario = inventarioRepository
                .findByProveedorIdAndEstado(proveedorId, EstadoBasico.ACTIVO).size();
        long totalPaquetes = paqueteRepository
                .findByProveedorIdAndEstado(proveedorId, EstadoBasico.ACTIVO).size();
        long totalEventosProgramados = eventoRepository.countByEstado(EstadoEvento.PROGRAMADO);
        long totalContratos = contratoRepository.findByProveedorId(proveedorId).size();

        Map<String, Long> contratosPorEstado = new HashMap<>();
        for (EstadoContrato estado : EstadoContrato.values()) {
            long count = contratoRepository
                    .findByProveedorIdAndEstado(proveedorId, estado).size();
            contratosPorEstado.put(estado.name(), count);
        }

        BigDecimal ingresosTotales = contratoRepository.sumIngresosTotales(proveedorId);

        long pagosPendientes = pagoRepository.countByEstado(EstadoPago.PENDIENTE);
        BigDecimal montoPendienteCobrar = pagoRepository.sumMontoPendiente();

        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        var proximosEventos = eventoMapper.toResponseList(
                eventoRepository.findByFechaEventoBetweenOrderByFechaEventoAscHoraInicioAsc(
                        hoy, hoy.plusDays(30)));
        var eventosDelMes = eventoMapper.toResponseList(
                eventoRepository.findCalendario(inicioMes, finMes));

        return DashboardResponse.builder()
                .totalClientes(totalClientes)
                .totalInventarioActivo(totalInventario)
                .totalPaquetesActivos(totalPaquetes)
                .totalEventosProgramados(totalEventosProgramados)
                .totalContratos(totalContratos)
                .contratosPorEstado(contratosPorEstado)
                .ingresosTotales(ingresosTotales)
                .pagosPendientes(pagosPendientes)
                .montoPendienteCobrar(montoPendienteCobrar)
                .proximosEventos(proximosEventos)
                .eventosDelMes(eventosDelMes)
                .build();
    }
}
