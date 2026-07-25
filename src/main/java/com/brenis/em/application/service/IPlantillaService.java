package com.brenis.em.application.service;

import com.brenis.em.domain.plantilla.PlantillaContrato;

import java.util.List;

public interface IPlantillaService {

    PlantillaContrato create(Long proveedorId, PlantillaContrato plantilla);

    PlantillaContrato update(Long id, PlantillaContrato datos);

    PlantillaContrato findById(Long id);

    List<PlantillaContrato> findAllByProveedor(Long proveedorId);

    void deactivate(Long id);

    void deleteById(Long id);
}
