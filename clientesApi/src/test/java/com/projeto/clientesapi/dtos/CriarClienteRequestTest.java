package com.projeto.clientesapi.dtos;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CriarClienteRequestTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void devePassarComDadosValidos() {
        CriarClienteRequest dto = new CriarClienteRequest();
        dto.setNome("João da Silva");
        dto.setEmail("joao@email.com");
        dto.setCpf("12345678901");
        dto.setDataNascimento(LocalDate.of(1990, 5, 20));

        Set<ConstraintViolation<CriarClienteRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void deveRetornarErroQuandoNomeForMenorQue8Caracteres() {
        CriarClienteRequest dto = new CriarClienteRequest();
        dto.setNome("João"); // inválido
        dto.setEmail("email@teste.com");
        dto.setCpf("12345678901");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<CriarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    public void deveRetornarErroQuandoEmailForInvalido() {
        CriarClienteRequest dto = new CriarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("emailteste.com");
        dto.setCpf("12345678901");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<CriarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void deveRetornarErroQuandoEmailEstiverEmBranco() {
        CriarClienteRequest dto = new CriarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("");
        dto.setCpf("12345678901");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<CriarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void deveRetornarErroQuandoCpfForInvalido() {
        CriarClienteRequest dto = new CriarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("email@teste.com");
        dto.setCpf("123456789");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<CriarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    public void deveRetornarErroQuandoCpfEstiverEmBranco(){
        CriarClienteRequest dto = new CriarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("email@teste.com");
        dto.setCpf("");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<CriarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    public void deveRetornarErroQuandoDataNascimentoEstiverEmBranco(){
        CriarClienteRequest dto = new CriarClienteRequest();
        dto.setNome("João da Silva"); 
        dto.setEmail("email@teste.com");
        dto.setCpf("12345678901");
        // Não define dataNascimento (deixa como null)

        Set<ConstraintViolation<CriarClienteRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dataNascimento")));
    }
}
