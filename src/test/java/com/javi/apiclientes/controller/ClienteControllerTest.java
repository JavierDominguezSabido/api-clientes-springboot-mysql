package com.javi.apiclientes.controller;

import com.javi.apiclientes.dto.ClienteRequestDTO;
import com.javi.apiclientes.dto.ClienteResponseDTO;
import com.javi.apiclientes.exception.ClienteNoEncontradoException;
import com.javi.apiclientes.exception.EmailDuplicadoException;
import com.javi.apiclientes.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    private ClienteResponseDTO ana() {
        return new ClienteResponseDTO(1L, "Ana", "López", "ana@email.com", "600111222", LocalDateTime.now());
    }

    @Test
    void listarTodos_devuelve200ConLaLista() throws Exception {
        when(clienteService.listarTodos()).thenReturn(List.of(ana()));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));
    }

    @Test
    void buscarPorId_inexistente_devuelve404() throws Exception {
        when(clienteService.buscarPorId(99L)).thenThrow(new ClienteNoEncontradoException(99L));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("Cliente no encontrado"));
    }

    @Test
    void crearCliente_valido_devuelve201() throws Exception {
        when(clienteService.crearCliente(any(ClienteRequestDTO.class))).thenReturn(ana());

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","apellidos":"López","email":"ana@email.com","telefono":"600111222"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void crearCliente_sinNombreYConEmailMal_devuelve400ConLosCampos() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"","email":"no-es-un-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nombre").exists())
                .andExpect(jsonPath("$.campos.email").exists());

        verify(clienteService, never()).crearCliente(any(ClienteRequestDTO.class));
    }

    @Test
    void crearCliente_conEmailRepetido_devuelve409() throws Exception {
        when(clienteService.crearCliente(any(ClienteRequestDTO.class)))
                .thenThrow(new EmailDuplicadoException("ana@email.com"));

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","email":"ana@email.com"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }

    @Test
    void eliminarCliente_existente_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService).eliminarCliente(1L);
    }

    @Test
    void eliminarCliente_inexistente_devuelve404() throws Exception {
        doThrow(new ClienteNoEncontradoException(7L)).when(clienteService).eliminarCliente(7L);

        mockMvc.perform(delete("/api/clientes/7"))
                .andExpect(status().isNotFound());
    }
}
