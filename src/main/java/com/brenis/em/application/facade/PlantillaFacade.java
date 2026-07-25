package com.brenis.em.application.facade;

import com.brenis.em.application.dto.request.PlantillaRequest;
import com.brenis.em.application.dto.response.PlantillaResponse;
import com.brenis.em.application.mapper.PlantillaMapper;
import com.brenis.em.application.service.PlantillaService;
import com.brenis.em.domain.plantilla.PlantillaContrato;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlantillaFacade {

    private final PlantillaService plantillaService;
    private final PlantillaMapper plantillaMapper;

    public PlantillaFacade(PlantillaService plantillaService, PlantillaMapper plantillaMapper) {
        this.plantillaService = plantillaService;
        this.plantillaMapper = plantillaMapper;
    }

    public PlantillaResponse create(Long proveedorId, PlantillaRequest request) {
        PlantillaContrato plantilla = PlantillaContrato.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .tipo(request.getTipo())
                .contenidoHtml(request.getContenidoHtml())
                .placeholders(request.getPlaceholders())
                .esDefault(request.getEsDefault())
                .build();

        return plantillaMapper.toResponse(plantillaService.create(proveedorId, plantilla));
    }

    public PlantillaResponse update(Long id, PlantillaRequest request) {
        PlantillaContrato plantilla = PlantillaContrato.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .tipo(request.getTipo())
                .contenidoHtml(request.getContenidoHtml())
                .placeholders(request.getPlaceholders())
                .esDefault(request.getEsDefault())
                .build();

        return plantillaMapper.toResponse(plantillaService.update(id, plantilla));
    }

    public PlantillaResponse getById(Long id) {
        return plantillaMapper.toResponse(plantillaService.getById(id));
    }

    public List<PlantillaResponse> getAllByProveedor(Long proveedorId) {
        return plantillaMapper.toResponseList(plantillaService.getAllByProveedor(proveedorId));
    }

    public void deactivate(Long id) {
        plantillaService.deactivate(id);
    }

    public void delete(Long id) {
        plantillaService.delete(id);
    }
}
