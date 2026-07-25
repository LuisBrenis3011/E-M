package com.brenis.em.domain.contrato;

import com.brenis.em.domain.inventario.Inventario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_contrato")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetalleContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id", nullable = false)
    private Inventario inventario;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "es_obsequio", nullable = false)
    @Builder.Default
    private Boolean esObsequio = false;

    @Column(name = "tipo_detalle", nullable = false, length = 20)
    @Builder.Default
    private String tipoDetalle = "INCLUYE";

    @Column(nullable = false)
    @Builder.Default
    private Integer orden = 0;
}
