package com.projeto.clientesapi.events;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.projeto.clientesapi.entities.Endereco;


import lombok.Data;

@Data
public class ClienteCriado {
    private String nome;
    private String email;
    private String cpf;
    private LocalDate dataNascimento;
    private List<Endereco> enderecos;
    private LocalDateTime dataHoraCriacao;
}