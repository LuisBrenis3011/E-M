package com.brenis.em.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContratoDocumentoResponse {

    private Long id;
    private Long contratoId;
    private Long plantillaId;
    private String urlPdf;
    private Integer version;
    private LocalDateTime fechaGeneracion;
}
