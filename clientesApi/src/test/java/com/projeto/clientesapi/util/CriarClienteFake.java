package com.projeto.clientesapi.util;

import java.util.UUID;

import com.github.javafaker.Faker;
import com.projeto.clientesapi.dtos.AtualizarClienteRequest;
import com.projeto.clientesapi.dtos.ClienteResponse;
import com.projeto.clientesapi.dtos.CriarClienteRequest;
import com.projeto.clientesapi.dtos.EnderecoResponse;
import com.projeto.clientesapi.entities.Cliente;

public class CriarClienteFake {
    private static final Faker faker = new Faker();

    public static Cliente criarCliente(){
        var cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome(faker.name().fullName());
        cliente.setEmail(faker.internet().emailAddress());
        cliente.setDataNascimento(faker.date().birthday().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        cliente.setEmail(faker.internet().emailAddress());
        cliente.setCpf(faker.number().digits(11));
        return cliente;
    }
    
    public static ClienteResponse criarClienteResponse(){
        var response = new ClienteResponse();
        response.setNome(faker.name().fullName());
        response.setCpf(faker.number().digits(11));
        response.setDataNascimento(faker.date().birthday().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        response.setEmail(faker.internet().emailAddress());
        response.setId(UUID.randomUUID());

        var enderecoResponse = CriarEnderecoFake.criarEnderecoResponse(response.getId());
        response.setEnderecos(new EnderecoResponse[] { enderecoResponse });
        return response;
    }

    public static CriarClienteRequest criarRequestDeCliente(){
        var request = new CriarClienteRequest();
        request.setNome(faker.name().fullName());
        request.setNome(faker.name().fullName());
        request.setCpf(faker.number().digits(11));
        request.setDataNascimento(faker.date().birthday().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        request.setEmail(faker.internet().emailAddress());

        var enderecoRequest = CriarEnderecoFake.criarEnderecoRequest();
        request.setEndereco(enderecoRequest);
        return request;

    }

    public static AtualizarClienteRequest criarAtualizarCliente(){
        var request = new AtualizarClienteRequest();
        request.setNome(faker.name().fullName());
        request.setCpf(faker.number().digits(11));
        request.setDataNascimento(faker.date().birthday().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        request.setEmail(faker.internet().emailAddress());
        request.setId(UUID.randomUUID());

        var enderecoRequest = CriarEnderecoFake.criarEnderecoRequest();
        request.setEndereco(enderecoRequest);
        return request;
    }

    


}
