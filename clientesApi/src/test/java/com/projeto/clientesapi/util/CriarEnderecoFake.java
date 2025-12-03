package com.projeto.clientesapi.util;

import java.util.UUID;

import com.github.javafaker.Faker;
import com.projeto.clientesapi.dtos.EnderecoRequest;
import com.projeto.clientesapi.dtos.EnderecoResponse;
import com.projeto.clientesapi.entities.Endereco;

public class CriarEnderecoFake {
    private static final Faker faker = new Faker();

    public static Endereco criarEndereco(){
        var endereco = new Endereco();
        endereco.setCep(faker.address().zipCode());
        endereco.setBairro(faker.address().cityName());
        endereco.setCidade(faker.address().cityName());
        endereco.setLogradouro(faker.address().streetName());
        endereco.setNumero(faker.address().streetAddressNumber());
        endereco.setUf(faker.address().stateAbbr());
        endereco.setComplemento(faker.address().secondaryAddress());
        endereco.setId(UUID.randomUUID());
        endereco.setCliente(CriarClienteFake.criarCliente());
        return endereco;
    }

    public static EnderecoRequest criarEnderecoRequest(){
        var request = new EnderecoRequest();
        request.setCep(faker.address().zipCode());
        request.setBairro(faker.address().cityName());
        request.setCidade(faker.address().cityName());
        request.setLogradouro(faker.address().streetName());
        request.setNumero(faker.address().streetAddressNumber());
        request.setUf(faker.address().stateAbbr());;
        request.setComplemento(faker.address().secondaryAddress());
        return request;
    }

    public static EnderecoResponse criarEnderecoResponse(UUID clienteId){
        var response = new EnderecoResponse();
        response.setCep(faker.address().zipCode());
        response.setBairro(faker.address().cityName());
        response.setCidade(faker.address().cityName());
        response.setLogradouro(faker.address().streetName());
        response.setNumero(faker.address().streetAddressNumber());
        response.setUf(faker.address().stateAbbr());
        response.setComplemento(faker.address().secondaryAddress());
        response.setIdCliente(clienteId);
        return response;
    }
   
    
}
