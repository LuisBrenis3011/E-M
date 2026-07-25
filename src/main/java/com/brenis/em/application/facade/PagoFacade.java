package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.PagoRequest;
import com.brenis.em.application.dto.response.PagoResponse;
import com.brenis.em.application.mapper.PagoMapper;
import com.brenis.em.application.service.PagoService;
import com.brenis.em.domain.contrato.Contrato;
import com.brenis.em.domain.pago.Pago;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class PagoFacade {

    private final PagoService pagoService;
    private final PagoMapper pagoMapper;

    public PagoFacade(PagoService pagoService, PagoMapper pagoMapper) {
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

    public PagoResponse getById(Long id) {
        return pagoMapper.toResponse(pagoService.getById(id));
    }

    public List<PagoResponse> getByContrato(Long contratoId) {
        return pagoMapper.toResponseList(pagoService.getByContrato(contratoId));
    }

    public List<PagoResponse> getPendientes() {
        return pagoMapper.toResponseList(pagoService.getPendientes());
    }
}
