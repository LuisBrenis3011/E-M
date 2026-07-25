package com.brenis.em.application.service;

import com.brenis.em.domain.contrato.Contrato;
import com.brenis.em.domain.contrato.DetalleContrato;
import com.brenis.em.domain.enums.EstadoContrato;

import java.math.BigDecimal;
import java.util.List;

public interface IContratoService {

    Contrato createFromPaquete(Long eventoId, Long paqueteId, Long proveedorId,
                               BigDecimal costoMovilidad, BigDecimal montoAdelanto);

    Contrato findById(Long id);

    Contrato findByEvento(Long eventoId);

    List<Contrato> findAllByProveedor(Long proveedorId);

    Contrato update(Long id, Contrato datos);

    Contrato cambiarEstado(Long id, EstadoContrato nuevoEstado);

    DetalleContrato addDetalle(Long contratoId, DetalleContrato detalle);

    void removeDetalle(Long detalleId);
}
