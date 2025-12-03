package com.projeto.clientesapi.exceptions;

public class PaginacaoInvalidaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int size;

    public PaginacaoInvalidaException(int size) {
        this.size = size;
    }

    @Override
    public String getMessage() {
        return "A quantidade máxima de registros permitida é 25, mas foi solicitado: " + size;
    }
    
}
