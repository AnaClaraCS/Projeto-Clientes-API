package com.projeto.clientesapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import com.projeto.clientesapi.dtos.ClienteResponse;
import com.projeto.clientesapi.dtos.EnderecoRequest;
import com.projeto.clientesapi.entities.Endereco;
import com.projeto.clientesapi.exceptions.EnderecoNaoEncontradoException;
import com.projeto.clientesapi.exceptions.PaginacaoInvalidaException;
import com.projeto.clientesapi.repositories.EnderecoRepository;
import com.projeto.clientesapi.services.ClienteService;
import com.projeto.clientesapi.services.EnderecoService;
import com.projeto.clientesapi.util.CriarClienteFake;
import com.projeto.clientesapi.util.CriarEnderecoFake;

public class EnderecoServiceTest {

    private EnderecoRepository enderecoRepository;
    private ClienteService clienteService;

    @BeforeEach
    public void setUp(){
        enderecoRepository = mock(EnderecoRepository.class);
        clienteService = mock(ClienteService.class);
    }
    
    @Test
    @DisplayName("Deve criar endereço com sucesso")
    public void criarEnderecoComSucesso(){
        var modelMapper = new ModelMapper();
        //Arrange
        //Criando um EnderecoRequest e um Cliente
        var request = CriarEnderecoFake.criarEnderecoRequest();
        var cliente = CriarClienteFake.criarCliente();
        var clienteResponse = modelMapper.map(cliente, ClienteResponse.class);
        //Criando Endereço a partir do request
        var endereco = modelMapper.map(request, Endereco.class);
        endereco.setId(UUID.randomUUID());
        endereco.setCliente(cliente);
        // Definindo resposta do mock
        when(clienteService.buscarCliente(any(UUID.class))).thenReturn(clienteResponse);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        //Act
        var enderecoService = new EnderecoService(enderecoRepository);
        var result = enderecoService.criarEndereco(request, cliente);

        //Assert
        assertNotNull(result);
        assertEquals(result.getId(), endereco.getId());
        assertEquals(result.getLogradouro(), endereco.getLogradouro());
        assertEquals(result.getBairro(), endereco.getBairro());
        assertEquals(result.getCidade(), endereco.getCidade());
        assertEquals(result.getCep(), endereco.getCep());
        assertEquals(result.getNumero(), endereco.getNumero()); 
        assertEquals(result.getUf(), endereco.getUf());   
        assertEquals(result.getIdCliente(), endereco.getCliente().getId());   
        assertEquals(result.getComplemento(), endereco.getComplemento()); 
    }

    @Test
    @DisplayName("Deve buscar endereço com sucesso")
    public void buscarEndereco(){
        //Arrange
        //Criando um id para buscar
        var idEndereco = UUID.randomUUID();
        //Definindo retorno do mock
        var endereco = CriarEnderecoFake.criarEndereco();
        endereco.setId(idEndereco);
        when(enderecoRepository.findById(any(UUID.class))).thenReturn(Optional.of(endereco));

        //Act
        var enderecoService = new EnderecoService(enderecoRepository);
        var result = enderecoService.buscarEndereco(idEndereco);

        //Assert
        assertNotNull(result);
        assertEquals(result.getId(), endereco.getId());
        assertEquals(result.getLogradouro(), endereco.getLogradouro());
        assertEquals(result.getBairro(), endereco.getBairro());
        assertEquals(result.getCidade(), endereco.getCidade());
        assertEquals(result.getCep(), endereco.getCep());
        assertEquals(result.getNumero(), endereco.getNumero());
        assertEquals(result.getIdCliente(), endereco.getCliente().getId());   
        assertEquals(result.getComplemento(), endereco.getComplemento()); 
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar cliente por ID")
    public void naoEncontrarCliente(){
        //Arrange
        //Criando um id para buscar
        var idEndereco = UUID.randomUUID();
        //Definindo retorno do mock
        when(enderecoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        //Act & Assert
        var enderecoService = new EnderecoService(enderecoRepository);
        assertThrows(EnderecoNaoEncontradoException.class, () -> {
            enderecoService.buscarEndereco(idEndereco);
        });
    }

    @Test
    @DisplayName("Deve atualizar endereço com sucesso")
    public void atualizarEndereco(){
        // Arrange
        //Criando um endereço simulando dados já salvos
        var endereco = CriarEnderecoFake.criarEndereco();
        
        // Criando um EndereçoRequest com novos valores de bairro, cidade, rua e numero
        var faker = new Faker();
        var request = new EnderecoRequest();
        request.setBairro(faker.address().cityName());
        request.setCidade(faker.address().cityName());
        request.setLogradouro(faker.address().streetName());
        request.setNumero(faker.address().streetAddressNumber());

        // Definindo resposta do mock
        when(enderecoRepository.findById(any(UUID.class))).thenReturn(Optional.of(endereco));
        
        // Act
        var enderecoService = new EnderecoService(enderecoRepository);
        var result = enderecoService.atualizarEndereco(endereco.getId(), request);

        // Assert
        assertNotNull(result);
        // verificando se os novos valores foram salvos
        assertEquals(result.getBairro(), request.getBairro());
        assertEquals(result.getCidade(), request.getCidade());
        assertEquals(result.getNumero(), request.getNumero());
        assertEquals(result.getLogradouro(), request.getLogradouro());

        // verificando se valores antigos se manteram
        assertEquals(result.getCep(), endereco.getCep());
        assertEquals(result.getId(), endereco.getId());
        assertEquals(result.getIdCliente(), endereco.getCliente().getId());   
        assertEquals(result.getComplemento(), endereco.getComplemento()); 
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cliente inexistente")
    public void naoAtualizarClienteInexistente() {
         //Arrange
        //Criando um id para buscar
        var idEndereco = UUID.randomUUID();
        var enderecoRequest = CriarEnderecoFake.criarEnderecoRequest();
       //Definindo retorno do mock
        when(enderecoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        //Act & Assert
        var enderecoService = new EnderecoService(enderecoRepository);
        assertThrows(EnderecoNaoEncontradoException.class, () -> {
            enderecoService.atualizarEndereco(idEndereco, enderecoRequest);
        });
    }

    @Test
    @DisplayName("Deve excluir endereço com sucesso")
    public void excluirEndereco(){
        //Arrange
        var endereco= CriarEnderecoFake.criarEndereco();
        when(enderecoRepository.findById(any(UUID.class))).thenReturn(Optional.of(endereco));

        //Act
        var endereService = new EnderecoService(enderecoRepository);
        var result = endereService.excluirEndereco(endereco.getId());

        //Arrange
        assertNotNull(result);
        assertEquals(result.getId(), endereco.getId());
        assertEquals(result.getLogradouro(), endereco.getLogradouro());
        assertEquals(result.getBairro(), endereco.getBairro());
        assertEquals(result.getCidade(), endereco.getCidade());
        assertEquals(result.getCep(), endereco.getCep());
        assertEquals(result.getNumero(), endereco.getNumero());
        assertEquals(result.getIdCliente(), endereco.getCliente().getId());   
        assertEquals(result.getComplemento(), endereco.getComplemento());
        //Verifica se o delete foi usado com endereço
        verify(enderecoRepository).delete(endereco);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir endereço inexistente")
    public void naoExcluirClienteInexistente() {
        //Arrange
        var idEndereco = UUID.randomUUID();
        when(enderecoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        //Act
        var enderecoService = new EnderecoService(enderecoRepository);
         assertThrows(EnderecoNaoEncontradoException.class, () -> {
            enderecoService.excluirEndereco(idEndereco);
        });
    }

    @Test
    @DisplayName("Deve consultar endereços com paginação")
    public void consultarEndereco(){
        // Arrange
        //Criando paginação
        int page = 0, size = 2;
        String sortBy = "cidade", direction = "asc";
        var pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        //Criando endereços da pagina
        var endereco1 = CriarEnderecoFake.criarEndereco();
        endereco1.setCidade("Aracaju");
        var endereco2 = CriarEnderecoFake.criarEndereco();
        endereco2.setCidade("Belém");

        var enderecosPage = new PageImpl<>(List.of(endereco1, endereco2), pageable, 2);

        when(enderecoRepository.findAll(pageable)).thenReturn(enderecosPage);

        //Act
        var enderecoService = new EnderecoService(enderecoRepository);
        var result = enderecoService.consultar(page, size, sortBy, direction);

        //Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Aracaju", result.getContent().get(0).getCidade());
        assertEquals("Belém", result.getContent().get(1).getCidade());
        verify(enderecoRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar endereços de um cliente com sucesso")
    public void buscarEnderecoCliente(){
        //Arrange
        //Crio uma lista de endereços para um cliente
        var cliente =CriarClienteFake.criarCliente();
        var endereco1 = CriarEnderecoFake.criarEndereco();
        endereco1.setCliente(cliente);
        var endereco2 = CriarEnderecoFake.criarEndereco();
        endereco2.setCliente(cliente);
        List<Endereco> enderecos = new ArrayList<>(java.util.Arrays.asList(endereco1, endereco2));

        when(enderecoRepository.findAllByCliente_Id(any(UUID.class))).thenReturn(enderecos);

        //Act
        var enderecoService = new EnderecoService(enderecoRepository);
        var result = enderecoService.buscarEnderecosCliente(cliente.getId());

        //Assert
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(endereco1.getId(), result[0].getId());
        assertEquals(endereco2.getId(), result[1].getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar endereços com paginação inválida")
    public void naoConsultarEnderecosComPaginacaoInvalida() { 
         var enderecoService = new EnderecoService(enderecoRepository);

        assertThrows(PaginacaoInvalidaException.class, () -> {
            enderecoService.consultar(0, 30, "nome", "asc");
        });
    
    }
}
