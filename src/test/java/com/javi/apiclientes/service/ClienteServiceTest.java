package com.javi.apiclientes.service;

import com.javi.apiclientes.dto.ClienteRequestDTO;
import com.javi.apiclientes.dto.ClienteResponseDTO;
import com.javi.apiclientes.entity.Cliente;
import com.javi.apiclientes.exception.ClienteNoEncontradoException;
import com.javi.apiclientes.exception.EmailDuplicadoException;
import com.javi.apiclientes.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteGuardado() {
        return new Cliente(1L, "Ana", "López", "ana@email.com", "600111222", LocalDateTime.now());
    }

    @Test
    void crearCliente_conEmailNuevo_loGuardaYDevuelveElDTO() {
        ClienteRequestDTO peticion = new ClienteRequestDTO("Ana", "López", "ana@email.com", "600111222");
        when(clienteRepository.existsByEmail("ana@email.com")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado());

        ClienteResponseDTO respuesta = clienteService.crearCliente(peticion);

        assertThat(respuesta.getId()).isEqualTo(1L);
        assertThat(respuesta.getEmail()).isEqualTo("ana@email.com");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void crearCliente_conEmailRepetido_lanzaExcepcionYNoGuarda() {
        ClienteRequestDTO peticion = new ClienteRequestDTO("Ana", "López", "ana@email.com", null);
        when(clienteRepository.existsByEmail("ana@email.com")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crearCliente(peticion))
                .isInstanceOf(EmailDuplicadoException.class);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void buscarPorId_inexistente_lanzaClienteNoEncontrado() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(ClienteNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void actualizarCliente_existente_cambiaLosDatos() {
        Cliente existente = clienteGuardado();
        ClienteRequestDTO cambios = new ClienteRequestDTO("Ana María", "López", "ana.maria@email.com", "699000111");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.existsByEmailAndIdNot("ana.maria@email.com", 1L)).thenReturn(false);
        when(clienteRepository.save(existente)).thenReturn(existente);

        ClienteResponseDTO respuesta = clienteService.actualizarCliente(1L, cambios);

        assertThat(respuesta.getNombre()).isEqualTo("Ana María");
        assertThat(respuesta.getEmail()).isEqualTo("ana.maria@email.com");
    }

    @Test
    void actualizarCliente_conEmailDeOtroCliente_lanzaExcepcion() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteGuardado()));
        when(clienteRepository.existsByEmailAndIdNot("otro@email.com", 1L)).thenReturn(true);
        ClienteRequestDTO cambios = new ClienteRequestDTO("Ana", null, "otro@email.com", null);

        assertThatThrownBy(() -> clienteService.actualizarCliente(1L, cambios))
                .isInstanceOf(EmailDuplicadoException.class);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void eliminarCliente_inexistente_lanzaExcepcionYNoBorra() {
        when(clienteRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.eliminarCliente(5L))
                .isInstanceOf(ClienteNoEncontradoException.class);
        verify(clienteRepository, never()).delete(any(Cliente.class));
    }

    @Test
    void buscarPorNombre_devuelveLosClientesQueCoinciden() {
        when(clienteRepository.findByNombreContainingIgnoreCase("an")).thenReturn(List.of(clienteGuardado()));

        List<ClienteResponseDTO> resultado = clienteService.buscarPorNombre("an");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Ana");
    }
}
