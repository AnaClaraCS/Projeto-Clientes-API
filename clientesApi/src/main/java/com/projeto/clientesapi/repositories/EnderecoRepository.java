package com.projeto.clientesapi.repositories;

import com.projeto.clientesapi.entities.Endereco;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository  extends JpaRepository<Endereco, UUID>{

    List<Endereco> findAllByCliente_Id(UUID idCliente);

    // @Query("""
    //     SELECT e FROM Endereco e
    //     WHERE e.idCliente = :idCliente
    //     """)
    // Page<Endereco> findAllByCliente(UUID idCliente, PageRequest pageable);
    
}
