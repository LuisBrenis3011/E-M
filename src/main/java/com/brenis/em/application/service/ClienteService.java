package com.brenis.em.application.service;

import com.brenis.em.domain.cliente.Cliente;
import com.brenis.em.domain.repository.ClienteRepository;
import com.brenis.em.domain.repository.ProveedorRepository;
import com.brenis.em.infrastructure.exception.BusinessException;
import com.brenis.em.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          ProveedorRepository proveedorRepository) {
        this.clienteRepository = clienteRepository;
        this.proveedorRepository = proveedorRepository;
    }

    public Cliente create(Long proveedorId, Cliente cliente) {
        cliente.setProveedor(proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", proveedorId)));

        if (clienteRepository.findByDniAndProveedorId(cliente.getDni(), proveedorId).isPresent()) {
            throw new BusinessException("Ya existe un cliente con ese DNI para este proveedor");
        }

        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente datos) {
        Cliente existente = getById(id);
        existente.setNombreCompleto(datos.getNombreCompleto());
        existente.setDni(datos.getDni());
        existente.setTelefono(datos.getTelefono());
        existente.setDireccion(datos.getDireccion());
        existente.setReferencia(datos.getReferencia());
        existente.setEmail(datos.getEmail());
        return clienteRepository.save(existente);
    }

    public Cliente getById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    public List<Cliente> getAllByProveedor(Long proveedorId) {
        return clienteRepository.findByProveedorId(proveedorId);
    }

    public List<Cliente> search(Long proveedorId, String query) {
        if (query == null || query.isBlank()) {
            return getAllByProveedor(proveedorId);
        }
        return clienteRepository.findByProveedorIdAndNombreCompletoContainingIgnoreCase(proveedorId, query);
    }

    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", id);
        }
        clienteRepository.deleteById(id);
    }
}
