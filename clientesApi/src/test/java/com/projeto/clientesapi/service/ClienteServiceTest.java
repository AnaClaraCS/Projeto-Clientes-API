package com.projeto.clientesapi.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.github.javafaker.Faker;
import com.projeto.clientesapi.components.RabbitMQProducer;
import com.projeto.clientesapi.dtos.AtualizarClienteRequest;
import com.projeto.clientesapi.dtos.EnderecoRequest;
import com.projeto.clientesapi.dtos.EnderecoResponse;
import com.projeto.clientesapi.entities.Cliente;
import com.projeto.clientesapi.repositories.ClienteRepository;
import com.projeto.clientesapi.services.ClienteService;
import com.projeto.clientesapi.services.EnderecoService;
import com.projeto.clientesapi.util.CriarClienteFake;
import com.projeto.clientesapi.exceptions.ClienteNaoEncontradoException;
import com.projeto.clientesapi.exceptions.CpfJaCadastradoException;
import com.projeto.clientesapi.exceptions.PaginacaoInvalidaException;

public class ClienteServiceTest {

    private ClienteRepository clienteRepository;
    private EnderecoService enderecoService;
    private RabbitMQProducer rabbitMQProducer;

    @BeforeEach
    public void setUp(){
        clienteRepository = mock(ClienteRepository.class);
        enderecoService = mock(EnderecoService.class);
        rabbitMQProducer = mock(RabbitMQProducer.class);
    }

    @Test
    @DisplayName("Cria cliente com sucesso")
    public void criarClienteComSucesso(){
        //Arrange
        //Criando um ClienteRequest
        var request = CriarClienteFake.criarRequestDeCliente();
        
        //Criando Cliente a partir do request
        var mapper = new ModelMapper();
        var cliente =  mapper.map(request, Cliente.class);
        cliente.setId(UUID.randomUUID());
        //Criando endereço response a partir do request
        var enderecoResponse = mapper.map(request.getEndereco(), EnderecoResponse.class);
        enderecoResponse.setId(UUID.randomUUID());
        enderecoResponse.setIdCliente(cliente.getId());
        EnderecoResponse[] enderecos = new EnderecoResponse[] { enderecoResponse };
        
        when(clienteRepository.existsByCpf(request.getCpf())).thenReturn(false);
        when(enderecoService.criarEndereco(any(EnderecoRequest.class), any(Cliente.class)))
            .thenReturn(enderecoResponse);
        when(enderecoService.buscarEnderecosCliente(any(UUID.class))).thenReturn(enderecos);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        //Act
        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);
        var response = clienteService.criarCliente(request);
       
        //Assert
        assertNotNull(response);
        assertEquals(cliente.getId(), response.getId());
        assertEquals(cliente.getNome(), response.getNome());
        assertEquals(cliente.getEmail(), response.getEmail());
        assertEquals(cliente.getDataNascimento(), response.getDataNascimento());
        assertEquals(cliente.getCpf(), response.getCpf());
        assertEquals(cliente.getId(), response.getId());
        assertArrayEquals(enderecos, response.getEnderecos());
    }

    @Test
    @DisplayName("Não deve criar cliente com CPF já existente")
    public void naoCriarClienteComCpfExistente(){
        //Arrange
        var request = CriarClienteFake.criarRequestDeCliente();
        // Retorna true para simular que o CPF já existe
        when(clienteRepository.existsByCpf(anyString())).thenReturn(true);
        
        //Act & Assert
        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);
        assertThrows(CpfJaCadastradoException.class, () -> {
            clienteService.criarCliente(request);
        });
    }
    
    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    public void buscarCliente(){
        //Arrange
        var cliente = CriarClienteFake.criarCliente();
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.of(cliente));
        
        //Act
        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);
        var response = clienteService.buscarCliente(cliente.getId());
       
        //Assert
        assertNotNull(response);
        assertEquals(cliente.getId(), response.getId());
        assertEquals(cliente.getNome(), response.getNome());
        assertEquals(cliente.getEmail(), response.getEmail());
        assertEquals(cliente.getDataNascimento(), response.getDataNascimento());
        assertEquals(cliente.getCpf(), response.getCpf());
        assertEquals(cliente.getId(), response.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar cliente por ID")
    public void naoEncontrarCliente(){
        //Arrange
        var idCliente = UUID.randomUUID();
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        
        //Act & Assert
        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);
        assertThrows(ClienteNaoEncontradoException.class, () -> {
            clienteService.buscarCliente(idCliente);
        });
    }

    @Test
    @DisplayName("Deve atualiza cliente com sucesso")
    public void atualizarClienteComSucesso() {
        //Arrange
        var cliente = CriarClienteFake.criarCliente();
        //Criando um request para atualização de nome e email
        var faker = new Faker();
        var request = new AtualizarClienteRequest();
        request.setNome(faker.name().fullName());
        request.setEmail(faker.internet().emailAddress());

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        //Act
        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);
        var response = clienteService.atualizarCliente(cliente.getId(), request);

        //Assert
        assertNotNull(response);
        //Verificando se salvou atualização
        assertEquals(request.getNome(), response.getNome());
        assertEquals(request.getEmail(), response.getEmail());
        //Verificando se manteve as outras informações
        assertEquals(cliente.getDataNascimento(), response.getDataNascimento());
        assertEquals(cliente.getCpf(), response.getCpf());
        assertEquals(cliente.getId(), response.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cliente inexistente")
    public void naoAtualizarClienteInexistente() {
        var id = UUID.randomUUID();
        var request = new AtualizarClienteRequest();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);

        assertThrows(ClienteNaoEncontradoException.class, () -> {
            clienteService.atualizarCliente(id, request);
        });
    }

    @Test
    @DisplayName("Deve excluir cliente com sucesso")
    public void excluirClienteComSucesso(){
        //Assert
        var cliente = CriarClienteFake.criarCliente();        
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.of(cliente));

        //Act
        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);
        var response = clienteService.excluirCliente(cliente.getId());

        //Assert
        assertNotNull(response);
        assertEquals(cliente.getId(), response.getId());
        assertEquals(cliente.getNome(), response.getNome());
        assertEquals(cliente.getEmail(), response.getEmail());
        assertEquals(cliente.getDataNascimento(), response.getDataNascimento());
        assertEquals(cliente.getCpf(), response.getCpf());
        assertEquals(cliente.getId(), response.getId());
        //Verifica se o delete foi usado com cliente
        verify(clienteRepository).delete(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir cliente inexistente")
    public void naoExcluirClienteInexistente() {
        var id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);

        assertThrows(ClienteNaoEncontradoException.class, () -> {
            clienteService.excluirCliente(id);
        });
    }

    @Test
    @DisplayName("Deve consultar clientes com paginação")
    public void consultarClientesComPaginacao() {
        // Arrange
        //Criando paginação
        int page = 0, size = 2;
        String sortBy = "nome", direction = "asc";
        var pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        //Criando clientes
        var cliente1 = CriarClienteFake.criarCliente();
        cliente1.setNome("Antonio");
        var cliente2 = CriarClienteFake.criarCliente();
        cliente2.setNome("Bruno");

        var clientesPage = new PageImpl<>(List.of(cliente1, cliente2), pageable, 2);

        when(clienteRepository.findAll(pageable)).thenReturn(clientesPage);
        when(enderecoService.buscarEnderecosCliente(any(UUID.class))).thenReturn(new EnderecoResponse[0]);

        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);
        var result = clienteService.consultar(page, size, sortBy, direction);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Antonio", result.getContent().get(0).getNome());
        assertEquals("Bruno", result.getContent().get(1).getNome());
        verify(clienteRepository).findAll(pageable);
        verify(enderecoService, times(2)).buscarEnderecosCliente(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar clientes com paginação inválida")
    public void naoConsultarClientesComPaginacaoInvalida() {
        var clienteService = new ClienteService(clienteRepository, enderecoService, rabbitMQProducer);

        assertThrows(PaginacaoInvalidaException.class, () -> {
            clienteService.consultar(0, 30, "nome", "asc");
        });
    }

}
