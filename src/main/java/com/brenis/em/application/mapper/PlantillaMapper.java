package com.brenis.em.application.mapper;

import com.brenis.em.application.dto.response.PlantillaResponse;
import com.brenis.em.domain.plantilla.PlantillaContrato;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlantillaMapper {

    PlantillaResponse toResponse(PlantillaContrato plantilla);

    List<PlantillaResponse> toResponseList(List<PlantillaContrato> plantillas);
}
