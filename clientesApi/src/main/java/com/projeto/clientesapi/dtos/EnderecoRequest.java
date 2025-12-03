package com.projeto.clientesapi.dtos;

import lombok.Data;

@Data
public class EnderecoRequest {
    private String logradouro;
    private String complemento;
    private String numero;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
}
