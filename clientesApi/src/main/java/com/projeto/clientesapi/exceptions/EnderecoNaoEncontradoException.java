package com.projeto.clientesapi.exceptions;

import java.util.UUID;

public class EnderecoNaoEncontradoException extends RuntimeException {

    private UUID id;
    public EnderecoNaoEncontradoException(UUID id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Endereço não encontrado para o ID: " + id.toString();
    }
    
}
