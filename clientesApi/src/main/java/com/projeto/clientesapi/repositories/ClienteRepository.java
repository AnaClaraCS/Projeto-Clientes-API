package com.projeto.clientesapi.repositories;

import com.projeto.clientesapi.entities.Cliente;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClienteRepository extends JpaRepository<Cliente, UUID>{

    boolean existsByCpf(String cpf);

    @Query("SELECT c FROM Cliente c JOIN FETCH c.enderecos ORDER BY c.nome ASC")
    List<Cliente> findAllClientesOrderedByNome();

    @Query("SELECT c FROM Cliente c WHERE c.id = :id")
    Page<Cliente> findClienteWithEndereco(UUID id, Pageable request);
    
}
