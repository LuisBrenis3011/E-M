package com.brenis.em.application.service;

import com.brenis.em.domain.pago.Pago;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPagoService {

    Pago create(Pago pago, MultipartFile comprobante);

    Pago verificar(Long pagoId, Long verificadorId);

    Pago rechazar(Long pagoId, String motivo);

    Pago findById(Long id);

    List<Pago> findByContrato(Long contratoId);

    List<Pago> findPendientes();
}
