package com.projeto.clientesapi.exceptions;


public class CpfJaCadastradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String cpf;
    public CpfJaCadastradoException(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String getMessage() {
        return "O cpf '" + cpf + "' já está cadastrado. Tente outro.";
    }
}

