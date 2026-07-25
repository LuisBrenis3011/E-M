package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.IPlantillaService;
import com.brenis.em.domain.enums.EstadoBasico;
import com.brenis.em.domain.plantilla.PlantillaContrato;
import com.brenis.em.domain.repository.PlantillaContratoRepository;
import com.brenis.em.domain.repository.ProveedorRepository;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlantillaServiceImpl implements IPlantillaService {

    private final PlantillaContratoRepository plantillaRepository;
    private final ProveedorRepository proveedorRepository;

    public PlantillaServiceImpl(PlantillaContratoRepository plantillaRepository,
                                ProveedorRepository proveedorRepository) {
        this.plantillaRepository = plantillaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public PlantillaContrato create(Long proveedorId, PlantillaContrato plantilla) {
        plantilla.setProveedor(proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", proveedorId)));

        if (Boolean.TRUE.equals(plantilla.getEsDefault())) {
            plantillaRepository.findByProveedorIdAndEsDefaultTrue(proveedorId)
                    .ifPresent(existing -> {
                        existing.setEsDefault(false);
                        plantillaRepository.save(existing);
                    });
        }

        return plantillaRepository.save(plantilla);
    }

    @Override
    public PlantillaContrato update(Long id, PlantillaContrato datos) {
        PlantillaContrato existente = findById(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setTipo(datos.getTipo());
        existente.setContenidoHtml(datos.getContenidoHtml());
        existente.setPlaceholders(datos.getPlaceholders());

        if (Boolean.TRUE.equals(datos.getEsDefault())) {
            plantillaRepository.findByProveedorIdAndEsDefaultTrue(existente.getProveedor().getId())
                    .ifPresent(e -> {
                        if (!e.getId().equals(id)) {
                            e.setEsDefault(false);
                            plantillaRepository.save(e);
                        }
                    });
        }
        existente.setEsDefault(datos.getEsDefault());

        return plantillaRepository.save(existente);
    }

    @Override
    public PlantillaContrato findById(Long id) {
        return plantillaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla", id));
    }

    @Override
    public List<PlantillaContrato> findAllByProveedor(Long proveedorId) {
        return plantillaRepository.findByProveedorIdAndEstado(proveedorId, EstadoBasico.ACTIVO);
    }

    @Override
    public void deactivate(Long id) {
        PlantillaContrato plantilla = findById(id);
        plantilla.setEstado(EstadoBasico.INACTIVO);
        plantillaRepository.save(plantilla);
    }

    @Override
    public void deleteById(Long id) {
        if (!plantillaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plantilla", id);
        }
        plantillaRepository.deleteById(id);
    }
}
