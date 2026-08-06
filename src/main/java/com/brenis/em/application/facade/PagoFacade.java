package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.PagoRequest;
import com.brenis.em.application.dto.response.PagoResponse;
import com.brenis.em.application.mapper.PagoMapper;
import com.brenis.em.application.service.IPagoService;
import com.brenis.em.domain.contrato.Contrato;
import com.brenis.em.domain.pago.Pago;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class PagoFacade {

    private final IPagoService pagoService;
    private final PagoMapper pagoMapper;

    public PagoFacade(IPagoService pagoService, PagoMapper pagoMapper) {
        this.pagoService = pagoService;
        this.pagoMapper = pagoMapper;
    }

    public PagoResponse create(PagoRequest request, MultipartFile comprobante) {
        Pago pago = Pago.builder()
                .contrato(Contrato.builder().id(request.getContratoId()).build())
                .tipoPago(request.getTipoPago())
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .codigoOperacion(request.getCodigoOperacion())
                .notas(request.getNotas())
                .build();

        return pagoMapper.toResponse(pagoService.create(pago, comprobante));
    }

    public PagoResponse verificar(Long pagoId, Long verificadorId) {
        return pagoMapper.toResponse(pagoService.verificar(pagoId, verificadorId));
    }

    public PagoResponse rechazar(Long pagoId, String motivo) {
        return pagoMapper.toResponse(pagoService.rechazar(pagoId, motivo));
    }

    public PagoResponse findById(Long id) {
        return pagoMapper.toResponse(pagoService.findById(id));
    }

    public List<PagoResponse> findByContrato(Long contratoId) {
        return pagoMapper.toResponseList(pagoService.findByContrato(contratoId));
    }

    public List<PagoResponse> findPendientes() {
        return pagoMapper.toResponseList(pagoService.findPendientes());
    }

    public void deleteById(Long id) {
        pagoService.deleteById(id);
    }
}
