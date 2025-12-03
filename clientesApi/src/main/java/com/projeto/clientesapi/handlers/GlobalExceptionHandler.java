package com.projeto.clientesapi.handlers;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.projeto.clientesapi.exceptions.ClienteNaoEncontradoException;
import com.projeto.clientesapi.exceptions.CpfJaCadastradoException;
import com.projeto.clientesapi.exceptions.PaginacaoInvalidaException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Percorre os erros de campo e adiciona ao map
        ex.getBindingResult().getAllErrors().forEach(error -> {
        String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST) //HTTP 400
            .body(errors);
    }

    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<Object> handleCpfJaCadastrado (CpfJaCadastradoException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT) //HTTP 409
            .body(ex.getMessage());
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<Object> handleClienteNaoEncontradoException (ClienteNaoEncontradoException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND) //HTTP 404
            .body(ex.getMessage());
    }

    @ExceptionHandler(PaginacaoInvalidaException.class)
    public ResponseEntity<Object> handlePaginacaoInvalidaException (PaginacaoInvalidaException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST) //HTTP 400
            .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Ocorreu um erro inesperado: " + ex.getMessage());
    }


}
