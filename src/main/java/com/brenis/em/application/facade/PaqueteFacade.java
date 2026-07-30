package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.DetallePaqueteRequest;
import com.brenis.em.application.dto.request.PaqueteRequest;
import com.brenis.em.application.dto.response.PaqueteResponse;
import com.brenis.em.application.mapper.PaqueteMapper;
import com.brenis.em.application.service.IPaqueteService;
import com.brenis.em.domain.categoria.Categoria;
import com.brenis.em.domain.inventario.Inventario;
import com.brenis.em.domain.paquete.DetallePaquete;
import com.brenis.em.domain.paquete.Paquete;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaqueteFacade {

    private final IPaqueteService paqueteService;
    private final PaqueteMapper paqueteMapper;

    public PaqueteFacade(IPaqueteService paqueteService, PaqueteMapper paqueteMapper) {
        this.paqueteService = paqueteService;
        this.paqueteMapper = paqueteMapper;
    }

    public PaqueteResponse create(Long proveedorId, PaqueteRequest request) {
        Paquete paquete = Paquete.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precioBase(request.getPrecioBase())
                .duracionBaseHoras(request.getDuracionBaseHoras())
                .categoria(Categoria.builder().id(request.getCategoriaId()).build())
                .build();

        paquete = paqueteService.create(proveedorId, paquete);

        if (request.getDetalles() != null) {
            for (DetallePaqueteRequest detReq : request.getDetalles()) {
                DetallePaquete det = DetallePaquete.builder()
                        .inventario(Inventario.builder().id(detReq.getInventarioId()).build())
                        .cantidadIncluida(detReq.getCantidadIncluida())
                        .precioUnitario(detReq.getPrecioUnitario())
                        .esObsequio(detReq.getEsObsequio())
                        .orden(detReq.getOrden())
                        .build();
                paqueteService.addDetalle(paquete.getId(), det);
            }
        }

        return paqueteMapper.toResponse(paqueteService.findById(paquete.getId()));
    }

    public PaqueteResponse update(Long id, PaqueteRequest request) {
        Paquete paquete = Paquete.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precioBase(request.getPrecioBase())
                .duracionBaseHoras(request.getDuracionBaseHoras())
                .categoria(Categoria.builder().id(request.getCategoriaId()).build())
                .build();

        return paqueteMapper.toResponse(paqueteService.update(id, paquete));
    }

    public PaqueteResponse findById(Long id) {
        return paqueteMapper.toResponse(paqueteService.findById(id));
    }

    public List<PaqueteResponse> findAllByProveedor(Long proveedorId) {
        return paqueteMapper.toResponseList(paqueteService.findAllByProveedor(proveedorId));
    }

    public List<PaqueteResponse> findByCategoria(Long categoriaId) {
        return paqueteMapper.toResponseList(paqueteService.findByCategoria(categoriaId));
    }

    public void deactivate(Long id) {
        paqueteService.deactivate(id);
    }
}
