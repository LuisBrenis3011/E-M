package com.brenis.em.application.service.impl;

import com.brenis.em.application.service.IPaqueteService;
import com.brenis.em.domain.enums.EstadoBasico;
import com.brenis.em.domain.inventario.Inventario;
import com.brenis.em.domain.paquete.DetallePaquete;
import com.brenis.em.domain.paquete.Paquete;
import com.brenis.em.domain.repository.*;
import com.brenis.em.infrastructure.exception.BusinessException;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaqueteServiceImpl implements IPaqueteService {

    private final PaqueteRepository paqueteRepository;
    private final DetallePaqueteRepository detallePaqueteRepository;
    private final ProveedorRepository proveedorRepository;
    private final CategoriaRepository categoriaRepository;
    private final TematicaRepository tematicaRepository;
    private final InventarioRepository inventarioRepository;

    public PaqueteServiceImpl(PaqueteRepository paqueteRepository,
                              DetallePaqueteRepository detallePaqueteRepository,
                              ProveedorRepository proveedorRepository,
                              CategoriaRepository categoriaRepository,
                              TematicaRepository tematicaRepository,
                              InventarioRepository inventarioRepository) {
        this.paqueteRepository = paqueteRepository;
        this.detallePaqueteRepository = detallePaqueteRepository;
        this.proveedorRepository = proveedorRepository;
        this.categoriaRepository = categoriaRepository;
        this.tematicaRepository = tematicaRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public Paquete create(Long proveedorId, Paquete paquete) {
        paquete.setProveedor(proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", proveedorId)));

        if (paquete.getCategoria() != null && paquete.getCategoria().getId() != null) {
            paquete.setCategoria(categoriaRepository.findById(paquete.getCategoria().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", paquete.getCategoria().getId())));
        }

        if (paquete.getTematica() != null && paquete.getTematica().getId() != null) {
            paquete.setTematica(tematicaRepository.findById(paquete.getTematica().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tematica", paquete.getTematica().getId())));
        }

        return paqueteRepository.save(paquete);
    }

    @Override
    public Paquete update(Long id, Paquete datos) {
        Paquete existente = findById(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecioBase(datos.getPrecioBase());
        existente.setDuracionBaseHoras(datos.getDuracionBaseHoras());

        if (datos.getCategoria() != null && datos.getCategoria().getId() != null) {
            existente.setCategoria(categoriaRepository.findById(datos.getCategoria().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", datos.getCategoria().getId())));
        }

        if (datos.getTematica() != null && datos.getTematica().getId() != null) {
            existente.setTematica(tematicaRepository.findById(datos.getTematica().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tematica", datos.getTematica().getId())));
        } else {
            existente.setTematica(null);
        }

        return paqueteRepository.save(existente);
    }

    @Override
    public Paquete findById(Long id) {
        return paqueteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paquete", id));
    }

    @Override
    public List<Paquete> findAllByProveedor(Long proveedorId) {
        return paqueteRepository.findByProveedorIdAndEstado(proveedorId, EstadoBasico.ACTIVO);
    }

    @Override
    public List<Paquete> findByCategoria(Long categoriaId) {
        return paqueteRepository.findByCategoriaId(categoriaId);
    }

    @Override
    public DetallePaquete addDetalle(Long paqueteId, DetallePaquete detalle) {
        Paquete paquete = findById(paqueteId);
        detalle.setPaquete(paquete);

        if (detalle.getInventario() != null && detalle.getInventario().getId() != null) {
            Inventario inventario = inventarioRepository.findById(detalle.getInventario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventario", detalle.getInventario().getId()));

            if (!inventario.getProveedor().getId().equals(paquete.getProveedor().getId())) {
                throw new BusinessException("El item de inventario no pertenece al mismo proveedor");
            }

            detalle.setInventario(inventario);

            if (detalle.getPrecioUnitario() == null) {
                detalle.setPrecioUnitario(inventario.getPrecioReferencial());
            }
        }

        return detallePaqueteRepository.save(detalle);
    }

    @Override
    public void removeDetalle(Long detalleId) {
        if (!detallePaqueteRepository.existsById(detalleId)) {
            throw new ResourceNotFoundException("DetallePaquete", detalleId);
        }
        detallePaqueteRepository.deleteById(detalleId);
    }

    @Override
    public void deactivate(Long id) {
        Paquete paquete = findById(id);
        paquete.setEstado(EstadoBasico.INACTIVO);
        paqueteRepository.save(paquete);
    }
}
