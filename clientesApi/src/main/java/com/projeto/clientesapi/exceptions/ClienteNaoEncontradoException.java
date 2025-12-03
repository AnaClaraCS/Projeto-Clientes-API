package com.projeto.clientesapi.exceptions;

import java.util.UUID;

public class ClienteNaoEncontradoException extends RuntimeException {

    private UUID id;
    public ClienteNaoEncontradoException(UUID id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Cliente não encontrado para o ID: " + id.toString();
    }
    
}
