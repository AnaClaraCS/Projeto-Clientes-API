package com.projeto.email_cliente_api.dtos;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;;

@Data
public class ClienteRequest {
    private UUID id;
    private String nome;
    private String email;
    private String cpf;
    private LocalDate dataNascimento;
    private EnderecoRequest[] enderecos;
}
