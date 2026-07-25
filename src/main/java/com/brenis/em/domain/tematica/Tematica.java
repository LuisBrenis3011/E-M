package com.brenis.em.domain.tematica;

import com.brenis.em.domain.categoria.Categoria;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tematica")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Tematica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "imagen_referencial", length = 255)
    private String imagenReferencial;
}
