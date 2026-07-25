package com.brenis.em.domain.contrato;

import com.brenis.em.domain.enums.EstadoContrato;
import com.brenis.em.domain.evento.Evento;
import com.brenis.em.domain.paquete.Paquete;
import com.brenis.em.domain.proveedor.Proveedor;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contrato")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false, unique = true)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paquete_id")
    private Paquete paquete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoContrato estado;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "costo_movilidad", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoMovilidad = BigDecimal.ZERO;

    @Column(name = "monto_adelanto", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal montoAdelanto = BigDecimal.ZERO;

    @Column(name = "monto_pendiente", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPendiente;

    @Column(length = 100)
    private String duracion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleContrato> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) estado = EstadoContrato.BORRADOR;
        if (costoMovilidad == null) costoMovilidad = BigDecimal.ZERO;
        if (montoAdelanto == null) montoAdelanto = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
