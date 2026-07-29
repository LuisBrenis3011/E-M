package com.brenis.em.domain.plantilla;

import com.brenis.em.domain.enums.EstadoBasico;
import com.brenis.em.domain.enums.TipoPlantilla;
import com.brenis.em.domain.proveedor.Proveedor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "plantilla_contrato")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PlantillaContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPlantilla tipo;

    @Column(name = "contenido_html", nullable = false, columnDefinition = "TEXT")
    private String contenidoHtml;

    @Column(columnDefinition = "TEXT")
    private String placeholders;

    @Column(name = "es_default", nullable = false)
    @Builder.Default
    private Boolean esDefault = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBasico estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) estado = EstadoBasico.ACTIVO;
        if (tipo == null) tipo = TipoPlantilla.CONTRATO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
