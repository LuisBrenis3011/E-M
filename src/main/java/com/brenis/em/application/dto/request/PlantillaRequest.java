package com.brenis.em.application.dto.request;

import com.brenis.em.domain.enums.TipoPlantilla;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PlantillaRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    private String descripcion;

    @NotNull
    private TipoPlantilla tipo;

    @NotBlank
    private String contenidoHtml;

    private List<String> placeholders;

    private Boolean esDefault = false;
}
