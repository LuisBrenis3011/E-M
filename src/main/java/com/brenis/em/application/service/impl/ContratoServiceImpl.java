package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.IContratoService;
import com.brenis.em.domain.contrato.Contrato;
import com.brenis.em.domain.contrato.DetalleContrato;
import com.brenis.em.domain.documento.ContratoDocumento;
import com.brenis.em.domain.enums.EstadoContrato;
import com.brenis.em.domain.pago.Pago;
import com.brenis.em.domain.paquete.DetallePaquete;
import com.brenis.em.domain.paquete.Paquete;
import com.brenis.em.domain.repository.*;
import com.brenis.em.infrastructure.exception.BusinessException;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ContratoServiceImpl implements IContratoService {

    private final ContratoRepository contratoRepository;
    private final DetalleContratoRepository detalleContratoRepository;
    private final EventoRepository eventoRepository;
    private final PaqueteRepository paqueteRepository;
    private final ProveedorRepository proveedorRepository;
    private final DetallePaqueteRepository detallePaqueteRepository;
    private final InventarioRepository inventarioRepository;
    private final PagoRepository pagoRepository;
    private final ContratoDocumentoRepository documentoRepository;

    public ContratoServiceImpl(ContratoRepository contratoRepository,
                               DetalleContratoRepository detalleContratoRepository,
                               EventoRepository eventoRepository,
                               PaqueteRepository paqueteRepository,
                               ProveedorRepository proveedorRepository,
                               DetallePaqueteRepository detallePaqueteRepository,
                               InventarioRepository inventarioRepository,
                               PagoRepository pagoRepository,
                               ContratoDocumentoRepository documentoRepository) {
        this.contratoRepository = contratoRepository;
        this.detalleContratoRepository = detalleContratoRepository;
        this.eventoRepository = eventoRepository;
        this.paqueteRepository = paqueteRepository;
        this.proveedorRepository = proveedorRepository;
        this.detallePaqueteRepository = detallePaqueteRepository;
        this.inventarioRepository = inventarioRepository;
        this.pagoRepository = pagoRepository;
        this.documentoRepository = documentoRepository;
    }

    @Override
    public Contrato createFromPaquete(Long eventoId, Long proveedorId,
                                       BigDecimal costoMovilidad, BigDecimal montoAdelanto) {
        var evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventoId));
        var proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", proveedorId));

        Paquete paquete = evento.getPaquete();
        if (paquete == null) {
            throw new BusinessException("El evento no tiene un paquete asignado");
        }

        if (contratoRepository.findByEventoId(eventoId).isPresent()) {
            throw new BusinessException("El evento ya tiene un contrato asociado");
        }

        BigDecimal movilidad = costoMovilidad != null ? costoMovilidad : BigDecimal.ZERO;
        BigDecimal adelanto = montoAdelanto != null ? montoAdelanto : BigDecimal.ZERO;

        Contrato contrato = Contrato.builder()
                .evento(evento)
                .proveedor(proveedor)
                .montoTotal(paquete.getPrecioBase())
                .costoMovilidad(movilidad)
                .montoAdelanto(adelanto)
                .montoPendiente(paquete.getPrecioBase().subtract(adelanto))
                .estado(EstadoContrato.BORRADOR)
                .build();

        contrato = contratoRepository.save(contrato);
        copiarDetallesDesdePaquete(contrato, paquete);

        return contrato;
    }

    private void copiarDetallesDesdePaquete(Contrato contrato, Paquete paquete) {
        List<DetallePaquete> detallesPaquete = detallePaqueteRepository
                .findByPaqueteIdOrderByOrden(paquete.getId());

        for (DetallePaquete dp : detallesPaquete) {
            DetalleContrato dc = DetalleContrato.builder()
                    .contrato(contrato)
                    .inventario(dp.getInventario())
                    .cantidad(dp.getCantidadIncluida())
                    .precioUnitario(dp.getPrecioUnitario())
                    .subtotal(dp.getPrecioUnitario()
                            .multiply(BigDecimal.valueOf(dp.getCantidadIncluida())))
                    .esObsequio(dp.getEsObsequio())
                    .orden(dp.getOrden())
                    .tipoDetalle(dp.getEsObsequio() ? "OBSEQUIO" : "INCLUYE")
                    .build();
            detalleContratoRepository.save(dc);
        }
    }

    @Override
    public Contrato findById(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", id));
    }

    @Override
    public Contrato findByEvento(Long eventoId) {
        return contratoRepository.findByEventoId(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato para evento", eventoId));
    }

    @Override
    public List<Contrato> findAllByProveedor(Long proveedorId) {
        return contratoRepository.findByProveedorId(proveedorId);
    }

    @Override
    public List<Contrato> findAllByProveedor(Long proveedorId, LocalDate desde, LocalDate hasta) {
        return contratoRepository.findByProveedorIdAndFechaCreacionBetween(
                proveedorId, desde.atStartOfDay(), hasta.atTime(23, 59, 59));
    }

    @Override
    public Contrato update(Long id, Contrato datos) {
        Contrato existente = findById(id);

        if (datos.getMontoTotal() != null) existente.setMontoTotal(datos.getMontoTotal());
        if (datos.getCostoMovilidad() != null) existente.setCostoMovilidad(datos.getCostoMovilidad());
        if (datos.getMontoAdelanto() != null) existente.setMontoAdelanto(datos.getMontoAdelanto());
        if (datos.getDuracion() != null) existente.setDuracion(datos.getDuracion());
        if (datos.getObservaciones() != null) existente.setObservaciones(datos.getObservaciones());

        existente.setMontoPendiente(existente.getMontoTotal().subtract(existente.getMontoAdelanto()));

        return contratoRepository.save(existente);
    }

    @Override
    public Contrato cambiarEstado(Long id, EstadoContrato nuevoEstado) {
        Contrato contrato = findById(id);
        contrato.setEstado(nuevoEstado);
        return contratoRepository.save(contrato);
    }

    @Override
    public DetalleContrato addDetalle(Long contratoId, DetalleContrato detalle) {
        Contrato contrato = findById(contratoId);
        detalle.setContrato(contrato);

        if (detalle.getInventario() != null && detalle.getInventario().getId() != null) {
            var inventario = inventarioRepository.findById(detalle.getInventario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventario", detalle.getInventario().getId()));
            detalle.setInventario(inventario);
        }

        detalle.setSubtotal(detalle.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(detalle.getCantidad())));

        return detalleContratoRepository.save(detalle);
    }

    @Override
    public void removeDetalle(Long detalleId) {
        if (!detalleContratoRepository.existsById(detalleId)) {
            throw new ResourceNotFoundException("DetalleContrato", detalleId);
        }
        detalleContratoRepository.deleteById(detalleId);
    }

    @Override
    public void deleteById(Long id) {
        if (!contratoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrato", id);
        }
        List<Pago> pagos = pagoRepository.findByContratoId(id);
        pagoRepository.deleteAll(pagos);
        List<ContratoDocumento> docs = documentoRepository.findByContratoIdOrderByVersionDesc(id);
        documentoRepository.deleteAll(docs);
        contratoRepository.deleteById(id);
    }
}
