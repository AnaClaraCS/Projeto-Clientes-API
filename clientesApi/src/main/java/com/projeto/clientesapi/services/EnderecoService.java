package com.projeto.clientesapi.services;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import com.projeto.clientesapi.dtos.EnderecoRequest;
import com.projeto.clientesapi.dtos.EnderecoResponse;
import com.projeto.clientesapi.entities.Cliente;
import com.projeto.clientesapi.entities.Endereco;
import com.projeto.clientesapi.exceptions.EnderecoNaoEncontradoException;
import com.projeto.clientesapi.exceptions.PaginacaoInvalidaException;
import com.projeto.clientesapi.repositories.EnderecoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnderecoService {
    
    private final EnderecoRepository repository;

    public EnderecoResponse criarEndereco(EnderecoRequest request, Cliente cliente){
        var mapper = new ModelMapper();
        var endereco = mapper.map(request, Endereco.class);
        endereco.setCliente(cliente);
        endereco = repository.save(endereco);
        var response = mapper.map(endereco, EnderecoResponse.class);
        return response;
    }

    public EnderecoResponse buscarEndereco(UUID id){
        var endereco = repository.findById(id)
            .orElseThrow(() -> new EnderecoNaoEncontradoException(id));
        var mapper = new ModelMapper();
        var response = mapper.map(endereco, EnderecoResponse.class);
        return response;
    }

    public EnderecoResponse atualizarEndereco(UUID id, EnderecoRequest request){
        var endereco = repository.findById(id)
            .orElseThrow(() -> new EnderecoNaoEncontradoException(id));
        var mapper = new ModelMapper();
        mapper.map(request, endereco);
        repository.save(endereco);
        var response = mapper.map(endereco, EnderecoResponse.class);
        return response;
    }

    public EnderecoResponse excluirEndereco(UUID id){
        var endereco = repository.findById(id)
            .orElseThrow(() -> new EnderecoNaoEncontradoException(id));
        var mapper = new ModelMapper();
        repository.delete(endereco);
        var response = mapper.map(endereco, EnderecoResponse.class);
        return response;
    }

    public Page<EnderecoResponse> consultar(int page, int size, String sortBy, String direction) {
        if(size > 25)
            throw new PaginacaoInvalidaException(size);
        var sort = direction.equalsIgnoreCase("asc") ? 
                    Sort.by(sortBy).ascending() : 
                    Sort.by(sortBy).descending();
        var pageable = PageRequest.of(page, size, sort);
        var enderecos = repository.findAll(pageable);
        var mapper = new ModelMapper();
        return enderecos.map(p -> mapper.map(p, EnderecoResponse.class));
    }

    public EnderecoResponse[] buscarEnderecosCliente(UUID idCliente){
        var enderecos = repository.findAllByCliente_Id(idCliente);
        var mapper = new ModelMapper();
        return enderecos.stream()
            .map(endereco -> mapper.map(endereco, EnderecoResponse.class))
            .toArray(EnderecoResponse[]::new);        
    }
}
