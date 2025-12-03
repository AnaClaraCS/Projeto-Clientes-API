package com.projeto.clientesapi.controllers;

import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestBody;

import com.projeto.clientesapi.dtos.AtualizarClienteRequest;
import com.projeto.clientesapi.dtos.ClienteResponse;
import com.projeto.clientesapi.dtos.CriarClienteRequest;
import com.projeto.clientesapi.services.ClienteService;

import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
public class ClienteController {

	@Autowired ClienteService clienteService;

	@PostMapping
	@Operation(summary = "Criar um Cliente", description = "Endpoint para criar um novo cliente")
	public ResponseEntity<ClienteResponse> post(@RequestBody @Valid CriarClienteRequest request) {
		return ResponseEntity.ok(clienteService.criarCliente(request));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar um cliente", description = "Endpoint para atualizar um cliente existente")
	public ResponseEntity<ClienteResponse> put(@PathVariable UUID id, @RequestBody @Valid AtualizarClienteRequest request) {
		return ResponseEntity.ok(clienteService.atualizarCliente(id, request));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Excluir um cliente", description = "Endpoint para excluir um cliente existente")
	public ResponseEntity<ClienteResponse> delete(@PathVariable UUID id) {
		return ResponseEntity.ok(clienteService.excluirCliente(id));
	}

	@GetMapping
	@Operation(summary = "Obter clientes", description = "Endpoint para obter os clientes")
	public ResponseEntity<Page<ClienteResponse>> get(
		@RequestParam(defaultValue = "0") int pagina,
		@RequestParam(defaultValue = "25") int qtdRegistros,
		@RequestParam(defaultValue = "nome") String campoOrdenacao,
		@RequestParam(defaultValue = "asc") String tipoOrdenacao
	) {
		return ResponseEntity.ok(clienteService.consultar(pagina, qtdRegistros, campoOrdenacao, tipoOrdenacao));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obter cliente por ID", description = "Endpoint para obter um cliente pelo ID")
	public ResponseEntity<ClienteResponse> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(clienteService.buscarCliente(id));
	}

}
