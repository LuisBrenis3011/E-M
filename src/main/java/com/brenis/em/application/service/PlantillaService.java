package com.brenis.em.application.service;

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
public class PlantillaService {

    private final PlantillaContratoRepository plantillaRepository;
    private final ProveedorRepository proveedorRepository;

    public PlantillaService(PlantillaContratoRepository plantillaRepository,
                            ProveedorRepository proveedorRepository) {
        this.plantillaRepository = plantillaRepository;
        this.proveedorRepository = proveedorRepository;
    }

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

    public PlantillaContrato update(Long id, PlantillaContrato datos) {
        PlantillaContrato existente = getById(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setTipo(datos.getTipo());
        existente.setContenidoHtml(datos.getContenidoHtml());
        existente.setPlaceholders(datos.getPlaceholders());

        if (Boolean.TRUE.equals(datos.getEsDefault())) {
            plantillaRepository.findByProveedorIdAndEsDefaultTrue(
                    existente.getProveedor().getId())
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

    public PlantillaContrato getById(Long id) {
        return plantillaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla", id));
    }

    public List<PlantillaContrato> getAllByProveedor(Long proveedorId) {
        return plantillaRepository.findByProveedorIdAndEstado(proveedorId, EstadoBasico.ACTIVO);
    }

    public void deactivate(Long id) {
        PlantillaContrato plantilla = getById(id);
        plantilla.setEstado(EstadoBasico.INACTIVO);
        plantillaRepository.save(plantilla);
    }

    public void delete(Long id) {
        if (!plantillaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plantilla", id);
        }
        plantillaRepository.deleteById(id);
    }
}
