package com.javi.apiclientes.service;

import com.javi.apiclientes.dto.ClienteRequestDTO;
import com.javi.apiclientes.dto.ClienteResponseDTO;
import com.javi.apiclientes.entity.Cliente;
import com.javi.apiclientes.exception.ClienteNoEncontradoException;
import com.javi.apiclientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = buscarEntidadPorId(id);
        return convertirAResponseDTO(cliente);
    }

    public ClienteResponseDTO crearCliente(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = convertirAEntidad(clienteRequestDTO);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return convertirAResponseDTO(clienteGuardado);
    }

    public ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO clienteRequestDTO) {
    Cliente clienteExistente = buscarEntidadPorId(id);

    clienteExistente.setNombre(clienteRequestDTO.getNombre());
    clienteExistente.setApellidos(clienteRequestDTO.getApellidos());
    clienteExistente.setEmail(clienteRequestDTO.getEmail());
    clienteExistente.setTelefono(clienteRequestDTO.getTelefono());

    Cliente clienteActualizado = clienteRepository.save(clienteExistente);
    return convertirAResponseDTO(clienteActualizado);
    }

    public void eliminarCliente(Long id) {
        Cliente cliente = buscarEntidadPorId(id);
        clienteRepository.delete(cliente);
    }

    public List<ClienteResponseDTO> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    private Cliente buscarEntidadPorId(long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    private Cliente convertirAEntidad(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = new Cliente();

        cliente.setNombre(clienteRequestDTO.getNombre());
        cliente.setApellidos(clienteRequestDTO.getApellidos());
        cliente.setEmail(clienteRequestDTO.getEmail());
        cliente.setTelefono(clienteRequestDTO.getTelefono());

        return cliente;
    }

    private ClienteResponseDTO convertirAResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellidos(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getFechaRegistro()
        );
    }
}
