package com.projeto.clientesapi.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import com.projeto.clientesapi.components.RabbitMQProducer;
import com.projeto.clientesapi.dtos.AtualizarClienteRequest;
import com.projeto.clientesapi.dtos.ClienteResponse;
import com.projeto.clientesapi.dtos.CriarClienteRequest;
import com.projeto.clientesapi.entities.Cliente;
import com.projeto.clientesapi.events.ClienteCriado;
import com.projeto.clientesapi.exceptions.ClienteNaoEncontradoException;
import com.projeto.clientesapi.exceptions.CpfJaCadastradoException;
import com.projeto.clientesapi.repositories.ClienteRepository;

import jakarta.validation.Valid;

import com.projeto.clientesapi.exceptions.PaginacaoInvalidaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final EnderecoService enderecoService;
    private final RabbitMQProducer rabbitMQProducer;

    public ClienteResponse criarCliente(@Valid CriarClienteRequest request){
        if(repository.existsByCpf(request.getCpf())) {
            throw new CpfJaCadastradoException(request.getCpf());
        }
        
        var mapper = new ModelMapper();
        var cliente = mapper.map(request, Cliente.class);
        cliente = repository.save(cliente);
        
        enderecoService.criarEndereco(request.getEndereco(), cliente);

        var response = mapper.map(cliente, ClienteResponse.class);
        response.setEnderecos(enderecoService.buscarEnderecosCliente(cliente.getId()));

        var clienteCriado = mapper.map(cliente, ClienteCriado.class);
        clienteCriado.setDataHoraCriacao(LocalDateTime.now());
        rabbitMQProducer.send(clienteCriado);

        return response;
    }

    public ClienteResponse buscarCliente(UUID id){
        var cliente = repository.findById(id)
            .orElseThrow(() -> new ClienteNaoEncontradoException(id));
        var mapper = new ModelMapper();
        var response = mapper.map(cliente, ClienteResponse.class);
        response.setEnderecos(enderecoService.buscarEnderecosCliente(cliente.getId()));
        return response;
    }

    public ClienteResponse atualizarCliente(UUID id, @Valid AtualizarClienteRequest request){
        var cliente = repository.findById(id)
            .orElseThrow(() -> new ClienteNaoEncontradoException(id));
        var mapper = new ModelMapper();
        mapper.map(request, cliente);
        repository.save(cliente);
        var response = mapper.map(cliente, ClienteResponse.class);
        response.setEnderecos(enderecoService.buscarEnderecosCliente(cliente.getId()));
        return response;
    }

    public ClienteResponse excluirCliente(UUID id){
        var cliente = repository.findById(id)
            .orElseThrow(() -> new ClienteNaoEncontradoException(id));
        var mapper = new ModelMapper();
        var response = mapper.map(cliente, ClienteResponse.class);
        response.setEnderecos(enderecoService.buscarEnderecosCliente(cliente.getId()));
        repository.delete(cliente);
        return response;
    }

    public Page<ClienteResponse> consultar(int page, int size, String sortBy, String direction) {
        if(size > 25)
            throw new PaginacaoInvalidaException(size);
        var sort = direction.equalsIgnoreCase("asc") ? 
                    Sort.by(Sort.Order.asc(sortBy)) : 
                    Sort.by(Sort.Order.desc(sortBy));
        var pageable = PageRequest.of(page, size, sort);
        var clientes = repository.findAll(pageable);
        var mapper = new ModelMapper();
        return clientes.map(cliente -> {
            var response = mapper.map(cliente, ClienteResponse.class);
            response.setEnderecos(enderecoService.buscarEnderecosCliente(cliente.getId()));
            return response;
        });
    }    
}
