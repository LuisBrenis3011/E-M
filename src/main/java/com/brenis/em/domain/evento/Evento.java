package com.brenis.em.domain.evento;

import com.brenis.em.domain.categoria.Categoria;
import com.brenis.em.domain.cliente.Cliente;
import com.brenis.em.domain.enums.EstadoEvento;
import com.brenis.em.domain.tematica.Tematica;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "evento")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tematica_id")
    private Tematica tematica;

    @Column(name = "tipo_evento", length = 100)
    private String tipoEvento;

    @Column(name = "nombre_cumpleanero", length = 150)
    private String nombreCumpleanero;

    @Column(name = "edad_cumpleanero")
    private Integer edadCumpleanero;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDate fechaEvento;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin_estimada")
    private LocalTime horaFinEstimada;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(length = 255)
    private String referencia;

    @Column(name = "aforo_estimado")
    private Integer aforoEstimado;

    @Column(name = "color_calendario", nullable = false, length = 7)
    @Builder.Default
    private String colorCalendario = "#3B82F6";

    @Column(name = "notas_internas", columnDefinition = "TEXT")
    private String notasInternas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) estado = EstadoEvento.PROGRAMADO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
