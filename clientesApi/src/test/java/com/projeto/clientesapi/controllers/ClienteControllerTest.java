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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.clientesapi.dtos.AtualizarClienteRequest;
import com.projeto.clientesapi.dtos.ClienteResponse;
import com.projeto.clientesapi.dtos.CriarClienteRequest;
import com.projeto.clientesapi.dtos.EnderecoResponse;
import com.projeto.clientesapi.exceptions.ClienteNaoEncontradoException;
import com.projeto.clientesapi.exceptions.CpfJaCadastradoException;
import com.projeto.clientesapi.exceptions.PaginacaoInvalidaException;
import com.projeto.clientesapi.services.ClienteService;
import com.projeto.clientesapi.util.CriarClienteFake;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve criar cliente com sucesso e retornar cliente response")
    public void deveCriarClienteERetornarResponse() throws Exception{
        //Arrange - criando request e response
        var request = CriarClienteFake.criarRequestDeCliente();
        var mapper = new ModelMapper();
        var response = mapper.map(request, ClienteResponse.class);
        response.setId(UUID.randomUUID());
        response.setEnderecos(new EnderecoResponse[] { mapper.map(request.getEndereco(), EnderecoResponse.class) });
    
        when(clienteService.criarCliente(any(CriarClienteRequest.class))).thenReturn(response);

        // Act & Assert
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mockMvc.perform(post("/api/v1/clientes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId().toString()))
            .andExpect(jsonPath("$.nome").value(request.getNome()))
            .andExpect(jsonPath("$.email").value(request.getEmail()))
            .andExpect(jsonPath("$.dataNascimento").value(request.getDataNascimento().toString()))
            .andExpect(jsonPath("$.cpf").value(request.getCpf()))
            .andExpect(jsonPath("$.enderecos").isArray())
            .andExpect(jsonPath("$.enderecos.length()").value(1));
    }

    @Test
    @DisplayName("Não deve criar cliente com campos inválidos e retorna erros de validação")
    public void naoDeveCriarClienteComCamposInvalidos() throws Exception {
        // Arrange - Criando request inválido
        var request = new CriarClienteRequest();
        request.setNome("Ana"); // inválido
        request.setEmail("email_invalido"); // inválido
        request.setCpf("123"); // inválido

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        // Act & Assert
        mockMvc.perform(post("/api/v1/clientes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.nome").value("Nome deve ter entre 8 e 100 caracteres"))
            .andExpect(jsonPath("$.email").value("Formato de email inválido"))
            .andExpect(jsonPath("$.cpf").value("CPF deve conter exatamente 11 dígitos numéricos"));
    }


    @Test
    @DisplayName("Não deve criar cliente com CPF duplicado e retorna mensagem de cpf já cadastrado")
    public void naoDeveCriarClienteComCpfDuplicado() throws Exception {
        var request = CriarClienteFake.criarRequestDeCliente();

        when(clienteService.criarCliente(any(CriarClienteRequest.class)))
            .thenThrow(new CpfJaCadastradoException(request.getCpf()));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        mockMvc.perform(post("/api/v1/clientes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$").value("O cpf '" + request.getCpf() + "' já está cadastrado. Tente outro."));
    }

    @Test
    @DisplayName("Retorna NotFound ao buscar cliente inexistente")
    public void deveRetornarNotFoundAoBuscarClienteInexistente() throws Exception {
        var id = UUID.randomUUID();

        when(clienteService.buscarCliente(any(UUID.class)))
            .thenThrow(new ClienteNaoEncontradoException(id));

        mockMvc.perform(get("/api/v1/clientes/" + id)
                .contentType("application/json"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").value("Cliente não encontrado para o ID: " + id));
    }

    @Test
    @DisplayName("Retorna BadRequest ao consultar com paginação inválida")
    public void deveRetornarBadRequestAoConsultarComPaginacaoInvalida() throws Exception {
        when(clienteService.consultar(0, 50, "nome", "asc"))
            .thenThrow(new PaginacaoInvalidaException(50));

        mockMvc.perform(get("/api/v1/clientes")
                .param("pagina", "0")
                .param("qtdRegistros", "50")
                .param("campoOrdenacao", "nome")
                .param("tipoOrdenacao", "asc")
                .contentType("application/json"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$").value("A quantidade máxima de registros permitida é 25, mas foi solicitado: 50"));
    }

    @Test
    @DisplayName("Altera cliente com sucesso e retorna cliente response")
    public void deveAlterarClienteERetornarResponse() throws Exception{
        //Arrange - criando request e response
        var request = CriarClienteFake.criarAtualizarCliente();
        var mapper = new ModelMapper();
        var response = mapper.map(request, ClienteResponse.class);
        response.setEnderecos(new EnderecoResponse[] { mapper.map(request.getEndereco(), EnderecoResponse.class) });
    
        when(clienteService.atualizarCliente(any(UUID.class),any(AtualizarClienteRequest.class))).thenReturn(response);

        // Act & Assert
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mockMvc.perform(put("/api/v1/clientes/" + request.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId().toString()))
            .andExpect(jsonPath("$.nome").value(request.getNome()))
            .andExpect(jsonPath("$.email").value(request.getEmail()))
            .andExpect(jsonPath("$.dataNascimento").value(request.getDataNascimento().toString()))
            .andExpect(jsonPath("$.cpf").value(request.getCpf()))
            .andExpect(jsonPath("$.enderecos").isArray())
            .andExpect(jsonPath("$.enderecos.length()").value(1));
    }
    
    @Test
    @DisplayName("Deve buscar clientes com paginação e retornar página de clientes")
    public void deveBuscarCliente() throws Exception {
        //Arrange - criando pagina com 2 clientes
        var cliente1 = CriarClienteFake.criarClienteResponse();
        var cliente2 = CriarClienteFake.criarClienteResponse();
        var page = new org.springframework.data.domain.PageImpl<>(
            java.util.List.of(cliente1, cliente2),
            org.springframework.data.domain.PageRequest.of(0, 2),
            2
        );
    
        when(clienteService.consultar(0, 2, "nome", "asc")).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/clientes")
            .param("pagina", "0")
            .param("qtdRegistros", "2")
            .param("campoOrdenacao", "nome")
            .param("tipoOrdenacao", "asc")
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    @DisplayName("Deve buscar cliente por ID e retornar cliente response")
    public void deveBuscarClienteERetornarResponse() throws Exception{
        //Arrange - criando request e response
        var response = CriarClienteFake.criarClienteResponse();
        
        when(clienteService.buscarCliente(any(UUID.class))).thenReturn(response);

        // Act & Assert
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mockMvc.perform(get("/api/v1/clientes/" + response.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(response)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId().toString()))
            .andExpect(jsonPath("$.nome").value(response.getNome()))
            .andExpect(jsonPath("$.email").value(response.getEmail()))
            .andExpect(jsonPath("$.dataNascimento").value(response.getDataNascimento().toString()))
            .andExpect(jsonPath("$.cpf").value(response.getCpf()))
            .andExpect(jsonPath("$.enderecos").isArray())
            .andExpect(jsonPath("$.enderecos.length()").value(1));
    }
}
