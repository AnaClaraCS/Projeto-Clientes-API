package com.projeto.clientesapi.controllers;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.clientesapi.dtos.EnderecoResponse;
import com.projeto.clientesapi.entities.Cliente;
import com.projeto.clientesapi.dtos.EnderecoRequest;
import com.projeto.clientesapi.services.ClienteService;
import com.projeto.clientesapi.services.EnderecoService;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/enderecos")
@Tag(name = "Enderecos", description = "Endpoints para gerenciamento de enderecos")
public class EnderecoController {

	@Autowired EnderecoService enderecoService;
    @Autowired ClienteService clienteService;

	@PostMapping("/{idCliente}")
	@Operation(summary = "Criar um Endereco", description = "Endpoint para criar um novo endereco")
	public ResponseEntity<EnderecoResponse> post(@PathVariable UUID idCliente, @RequestBody @Valid EnderecoRequest request) {
		var clienteResponse = clienteService.buscarCliente(idCliente);
		var mapper = new ModelMapper();
		var cliente = mapper.map(clienteResponse, Cliente.class);
		return ResponseEntity.ok(enderecoService.criarEndereco(request, cliente));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar um endereco", description = "Endpoint para atualizar um endereco existente")
	public ResponseEntity<EnderecoResponse> put(@PathVariable UUID id, @RequestBody @Valid EnderecoRequest request) {
		return ResponseEntity.ok(enderecoService.atualizarEndereco(id, request));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Excluir um endereco", description = "Endpoint para excluir um endereco existente")
	public ResponseEntity<EnderecoResponse> delete(@PathVariable UUID id) {
		return ResponseEntity.ok(enderecoService.excluirEndereco(id));
	}

	@GetMapping
	@Operation(summary = "Obter enderecos", description = "Endpoint para obter os enderecos")
	public ResponseEntity<Page<EnderecoResponse>> get(
		@RequestParam(defaultValue = "0") int pagina,
		@RequestParam(defaultValue = "25") int qtdRegistros,
		@RequestParam(defaultValue = "logradouro") String campoOrdenacao,
		@RequestParam(defaultValue = "asc") String tipoOrdenacao
	) {
		return ResponseEntity.ok(enderecoService.consultar(pagina, qtdRegistros, campoOrdenacao, tipoOrdenacao));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obter endereco por ID", description = "Endpoint para obter um endereco pelo ID")
	public ResponseEntity<EnderecoResponse> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(enderecoService.buscarEndereco(id));
	}

}
