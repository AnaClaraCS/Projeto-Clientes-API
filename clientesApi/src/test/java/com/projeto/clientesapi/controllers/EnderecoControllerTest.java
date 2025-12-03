package com.projeto.clientesapi.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.clientesapi.dtos.EnderecoRequest;
import com.projeto.clientesapi.dtos.EnderecoResponse;
import com.projeto.clientesapi.entities.Cliente;
import com.projeto.clientesapi.exceptions.PaginacaoInvalidaException;
import com.projeto.clientesapi.repositories.ClienteRepository;
import com.projeto.clientesapi.services.ClienteService;
import com.projeto.clientesapi.services.EnderecoService;
import com.projeto.clientesapi.util.CriarClienteFake;
import com.projeto.clientesapi.util.CriarEnderecoFake;

@WebMvcTest(EnderecoController.class)
public class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EnderecoService enderecoService;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private ClienteRepository clienteRepository;

    @Test
    @DisplayName("Deve criar endereco com sucesso e retornar endereco response")
    public void deveCriarEnderecoERetornarResponse() throws Exception{
        //Arrange - criando request e response
        var cliente = CriarClienteFake.criarClienteResponse();
        when(clienteService.buscarCliente(any(UUID.class))).thenReturn(cliente);

        var request = CriarEnderecoFake.criarEnderecoRequest();
        var mapper = new ModelMapper();
        var response = mapper.map(request, EnderecoResponse.class);
        response.setId(UUID.randomUUID());
        response.setIdCliente(cliente.getId());   
    
        when(enderecoService.criarEndereco(any(EnderecoRequest.class), any(Cliente.class)))
            .thenReturn(response);

        // Act & Assert
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mockMvc.perform(post("/api/v1/enderecos/" + cliente.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId().toString()))
            .andExpect(jsonPath("$.logradouro").value(request.getLogradouro()))
            .andExpect(jsonPath("$.numero").value(request.getNumero().toString()))
            .andExpect(jsonPath("$.bairro").value(request.getBairro()))
            .andExpect(jsonPath("$.cidade").value(request.getCidade()))
            .andExpect(jsonPath("$.uf").value(request.getUf()))
            .andExpect(jsonPath("$.cep").value(request.getCep().toString()));
    }

    @Test
    @DisplayName("Retorna BadRequest ao consultar com paginação inválida")
    public void deveRetornarBadRequestAoConsultarComPaginacaoInvalida() throws Exception {
        when( enderecoService.consultar(0, 50, "logradouro", "asc"))
            .thenThrow(new PaginacaoInvalidaException(50));

        mockMvc.perform(get("/api/v1/enderecos")
                .param("pagina", "0")
                .param("qtdRegistros", "50")
                .param("campoOrdenacao", "logradouro")
                .param("tipoOrdenacao", "asc")
                .contentType("application/json"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$").value("A quantidade máxima de registros permitida é 25, mas foi solicitado: 50"));
    }

    @Test
    @DisplayName("Altera endereco com sucesso e retorna endereco response")
    public void deveAlterarEnderecoERetornarResponse() throws Exception{
        //Arrange - criando request e response
        var request = CriarEnderecoFake.criarEnderecoRequest();
        var mapper = new ModelMapper();
        var response = mapper.map(request, EnderecoResponse.class);
        response.setId(UUID.randomUUID());
        when( enderecoService.atualizarEndereco(any(UUID.class),any(EnderecoRequest.class))).thenReturn(response);

        // Act & Assert
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mockMvc.perform(put("/api/v1/enderecos/" + response.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId().toString()))
            .andExpect(jsonPath("$.logradouro").value(request.getLogradouro()))
            .andExpect(jsonPath("$.numero").value(request.getNumero().toString()))
            .andExpect(jsonPath("$.bairro").value(request.getBairro()))
            .andExpect(jsonPath("$.cidade").value(request.getCidade()))
            .andExpect(jsonPath("$.uf").value(request.getUf()))
            .andExpect(jsonPath("$.cep").value(request.getCep().toString()));
    }

    @Test
    @DisplayName("Deve buscar endereco por ID e retornar endereco response")
    public void deveBuscarEnderecoERetornarResponse() throws Exception{
        //Arrange - criando response
        var response = CriarEnderecoFake.criarEnderecoResponse(UUID.randomUUID());
        response.setId(UUID.randomUUID());

        when( enderecoService.buscarEndereco(any(UUID.class))).thenReturn(response);

        // Act & Assert
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mockMvc.perform(get("/api/v1/enderecos/" + response.getId())
                .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId().toString()))
            .andExpect(jsonPath("$.logradouro").value(response.getLogradouro()))
            .andExpect(jsonPath("$.numero").value(response.getNumero().toString()))
            .andExpect(jsonPath("$.bairro").value(response.getBairro()))
            .andExpect(jsonPath("$.cidade").value(response.getCidade()))
            .andExpect(jsonPath("$.uf").value(response.getUf()))
            .andExpect(jsonPath("$.cep").value(response.getCep().toString()));
    }
}
