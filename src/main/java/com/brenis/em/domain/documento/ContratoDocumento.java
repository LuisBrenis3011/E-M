package com.brenis.em.domain.documento;

import com.brenis.em.domain.contrato.Contrato;
import com.brenis.em.domain.plantilla.PlantillaContrato;
import com.brenis.em.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contrato_documento")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ContratoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantilla_id", nullable = false)
    private PlantillaContrato plantilla;

    @Column(name = "contenido_html", columnDefinition = "TEXT")
    private String contenidoHtml;

    @Column(name = "url_pdf", length = 500)
    private String urlPdf;

    @Column(nullable = false)
    @Builder.Default
    private Integer version = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generado_por")
    private Usuario generadoPor;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @PrePersist
    protected void onCreate() {
        if (fechaGeneracion == null) fechaGeneracion = LocalDateTime.now();
    }
}
