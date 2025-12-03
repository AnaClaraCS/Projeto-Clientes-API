package com.projeto.email_cliente_api.dtos;

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
