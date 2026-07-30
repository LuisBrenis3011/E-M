package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.ContratoRequest;
import com.brenis.em.application.dto.request.DetalleContratoRequest;
import com.brenis.em.application.dto.response.ContratoResponse;
import com.brenis.em.application.mapper.ContratoMapper;
import com.brenis.em.application.service.IContratoService;
import com.brenis.em.domain.contrato.DetalleContrato;
import com.brenis.em.domain.enums.EstadoContrato;
import com.brenis.em.domain.inventario.Inventario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContratoFacade {

    private final IContratoService contratoService;
    private final ContratoMapper contratoMapper;

    public ContratoFacade(IContratoService contratoService, ContratoMapper contratoMapper) {
        this.contratoService = contratoService;
        this.contratoMapper = contratoMapper;
    }

    public ContratoResponse createFromPaquete(Long proveedorId, ContratoRequest request) {
        var contrato = contratoService.createFromPaquete(
                request.getEventoId(),
                proveedorId,
                request.getCostoMovilidad(),
                request.getMontoAdelanto());

        return contratoMapper.toResponse(contratoService.findById(contrato.getId()));
    }

    public ContratoResponse findById(Long id) {
        return contratoMapper.toResponse(contratoService.findById(id));
    }

    public ContratoResponse findByEvento(Long eventoId) {
        return contratoMapper.toResponse(contratoService.findByEvento(eventoId));
    }

    public List<ContratoResponse> findAllByProveedor(Long proveedorId) {
        return contratoMapper.toResponseList(contratoService.findAllByProveedor(proveedorId));
    }

    public ContratoResponse addDetalle(Long contratoId, DetalleContratoRequest request) {
        DetalleContrato det = DetalleContrato.builder()
                .inventario(Inventario.builder().id(request.getInventarioId()).build())
                .cantidad(request.getCantidad())
                .precioUnitario(request.getPrecioUnitario())
                .esObsequio(request.getEsObsequio())
                .tipoDetalle(request.getTipoDetalle())
                .orden(request.getOrden())
                .build();

        contratoService.addDetalle(contratoId, det);
        return contratoMapper.toResponse(contratoService.findById(contratoId));
    }

    public void removeDetalle(Long detalleId) {
        contratoService.removeDetalle(detalleId);
    }

    public ContratoResponse cambiarEstado(Long id, EstadoContrato estado) {
        return contratoMapper.toResponse(contratoService.cambiarEstado(id, estado));
    }
}
