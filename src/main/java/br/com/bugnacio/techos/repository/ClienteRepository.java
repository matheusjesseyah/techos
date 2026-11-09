package br.com.bugnacio.techos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bugnacio.techos.model.Cliente;

//Interface não é classe, Não escrevemos o spring Data JPA faz isso pra nós
//A interface JpaRepository já possui métodos prontos para salvar, buscar, atualizar, e deletar registros no banco de dados

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    boolean exisistsByTelefone(String telefone);
    
    boolean existsByTelefoneAndIdNot(String telefone, Long id);

    List<Cliente> findByNameContainingIgnoreCase(String nome);


}
