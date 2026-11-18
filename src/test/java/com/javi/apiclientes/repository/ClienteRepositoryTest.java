package com.javi.apiclientes.repository;

import com.javi.apiclientes.entity.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente nuevo(String nombre, String email) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setEmail(email);
        return cliente;
    }

    @Test
    void guardar_asignaLaFechaDeRegistroAutomaticamente() {
        Cliente guardado = clienteRepository.save(nuevo("Ana", "ana@email.com"));

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getFechaRegistro()).isNotNull();
    }

    @Test
    void findByNombreContainingIgnoreCase_encuentraSinDistinguirMayusculas() {
        clienteRepository.save(nuevo("Ana", "ana@email.com"));
        clienteRepository.save(nuevo("Mariana", "mariana@email.com"));
        clienteRepository.save(nuevo("Pedro", "pedro@email.com"));

        assertThat(clienteRepository.findByNombreContainingIgnoreCase("ANA"))
                .extracting(Cliente::getNombre)
                .containsExactlyInAnyOrder("Ana", "Mariana");
    }

    @Test
    void existsByEmail_y_existsByEmailAndIdNot_detectanDuplicados() {
        Cliente ana = clienteRepository.save(nuevo("Ana", "ana@email.com"));

        assertThat(clienteRepository.existsByEmail("ana@email.com")).isTrue();
        assertThat(clienteRepository.existsByEmail("otro@email.com")).isFalse();
        assertThat(clienteRepository.existsByEmailAndIdNot("ana@email.com", ana.getId())).isFalse();
        assertThat(clienteRepository.existsByEmailAndIdNot("ana@email.com", ana.getId() + 1)).isTrue();
    }
}
