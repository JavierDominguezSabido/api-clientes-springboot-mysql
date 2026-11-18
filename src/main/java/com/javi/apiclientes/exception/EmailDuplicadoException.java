package com.javi.apiclientes.exception;

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String email) {
        super("Ya existe un cliente con el email: " + email);
    }
}
