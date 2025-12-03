package com.projeto.clientesapi.dtos;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AtualizarClienteRequestTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void devePassarComDadosValidos() {
        AtualizarClienteRequest dto = new AtualizarClienteRequest();
        dto.setNome("João da Silva");
        dto.setEmail("joao@email.com");
        dto.setCpf("12345678901");
        dto.setDataNascimento(LocalDate.of(1990, 5, 20));

        Set<ConstraintViolation<AtualizarClienteRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void deveRetornarErroQuandoNomeForMenorQue8Caracteres() {
        AtualizarClienteRequest dto = new AtualizarClienteRequest();
        dto.setNome("João"); // inválido
        dto.setEmail("email@teste.com");
        dto.setCpf("12345678901");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<AtualizarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    public void deveRetornarErroQuandoEmailForInvalido() {
        AtualizarClienteRequest dto = new AtualizarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("emailteste.com");
        dto.setCpf("12345678901");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<AtualizarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void deveRetornarErroQuandoEmailEstiverEmBranco() {
        AtualizarClienteRequest dto = new AtualizarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("");
        dto.setCpf("12345678901");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<AtualizarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void deveRetornarErroQuandoCpfForInvalido() {
        AtualizarClienteRequest dto = new AtualizarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("email@teste.com");
        dto.setCpf("123456789");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<AtualizarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    public void deveRetornarErroQuandoCpfEstiverEmBranco(){
        AtualizarClienteRequest dto = new AtualizarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("email@teste.com");
        dto.setCpf("");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<AtualizarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    public void deveRetornarErroQuandoDataNascimentoEstiverEmBranco(){
        AtualizarClienteRequest dto = new AtualizarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("email@teste.com");
        dto.setCpf("12345678901");
        // Não define dataNascimento (deixa como null)

        Set<ConstraintViolation<AtualizarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dataNascimento")));
    }
}
