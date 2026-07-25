package com.brenis.em.domain.paquete;

import com.brenis.em.domain.inventario.Inventario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_paquete")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetallePaquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paquete_id", nullable = false)
    private Paquete paquete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id", nullable = false)
    private Inventario inventario;

    @Column(name = "cantidad_incluida", nullable = false)
    private Integer cantidadIncluida;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "es_obsequio", nullable = false)
    @Builder.Default
    private Boolean esObsequio = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer orden = 0;
}
