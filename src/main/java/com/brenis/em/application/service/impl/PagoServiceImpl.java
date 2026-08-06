package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.IPagoService;
import com.brenis.em.domain.contrato.Contrato;
import com.brenis.em.domain.enums.EstadoPago;
import com.brenis.em.domain.pago.Pago;
import com.brenis.em.domain.repository.ContratoRepository;
import com.brenis.em.domain.repository.PagoRepository;
import com.brenis.em.domain.repository.UsuarioRepository;
import com.brenis.em.infrastructure.exception.BusinessException;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import com.brenis.em.infrastructure.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PagoServiceImpl implements IPagoService {

    private final PagoRepository pagoRepository;
    private final ContratoRepository contratoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileStorageService fileStorageService;

    public PagoServiceImpl(PagoRepository pagoRepository,
                           ContratoRepository contratoRepository,
                           UsuarioRepository usuarioRepository,
                           FileStorageService fileStorageService) {
        this.pagoRepository = pagoRepository;
        this.contratoRepository = contratoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Pago create(Pago pago, MultipartFile comprobante) {
        var contrato = contratoRepository.findById(pago.getContrato().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", pago.getContrato().getId()));

        pago.setContrato(contrato);

        if (comprobante != null && !comprobante.isEmpty()) {
            validarArchivo(comprobante);
            String url = fileStorageService.storeComprobante(comprobante);
            pago.setUrlComprobante(url);
            pago.setNombreArchivo(comprobante.getOriginalFilename());
        }

        return pagoRepository.save(pago);
    }

    @Override
    public Pago verificar(Long pagoId, Long verificadorId) {
        Pago pago = findById(pagoId);

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new BusinessException("Solo se pueden verificar pagos pendientes");
        }

        var verificador = usuarioRepository.findById(verificadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", verificadorId));

        pago.setEstado(EstadoPago.VERIFICADO);
        pago.setVerificadoPor(verificador);
        pago.setFechaVerificacion(LocalDateTime.now());
        pago = pagoRepository.save(pago);

        recalcularMontoPendiente(pago.getContrato());

        return pago;
    }

    private void recalcularMontoPendiente(Contrato contrato) {
        var totalVerificado = pagoRepository.sumVerificadoByContrato(contrato.getId());
        contrato.setMontoPendiente(contrato.getMontoTotal().subtract(totalVerificado));
        contratoRepository.save(contrato);
    }

    @Override
    public Pago rechazar(Long pagoId, String motivo) {
        Pago pago = findById(pagoId);

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new BusinessException("Solo se pueden rechazar pagos pendientes");
        }

        pago.setEstado(EstadoPago.RECHAZADO);
        pago.setNotas((pago.getNotas() != null ? pago.getNotas() + " | " : "")
                + "Rechazado: " + motivo);

        return pagoRepository.save(pago);
    }

    @Override
    public Pago findById(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
    }

    @Override
    public List<Pago> findByContrato(Long contratoId) {
        return pagoRepository.findByContratoIdOrderByFechaPagoDesc(contratoId);
    }

    @Override
    public List<Pago> findPendientes() {
        return pagoRepository.findByEstado(EstadoPago.PENDIENTE);
    }

    @Override
    public void deleteById(Long id) {
        Pago pago = findById(id);
        if (pago.getUrlComprobante() != null) {
            fileStorageService.deleteFile(pago.getUrlComprobante());
        }
        pagoRepository.deleteById(id);
    }

    private void validarArchivo(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/")
                && !contentType.equals("application/pdf"))) {
            throw new BusinessException("Solo se permiten imagenes (JPG, PNG) y PDF");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("El archivo excede el tamano maximo de 5MB");
        }
    }
}
