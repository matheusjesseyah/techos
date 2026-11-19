package br.com.bugnacio.techos.service;

import br.com.bugnacio.techos.exception.RegraDeNegocioException;
import br.com.bugnacio.techos.repository.ClienteRepository;
import br.com.bugnacio.techos.repository.EquipamentoRepository;

//@Service informa ao Spring que esta classe contém regra de negocio.
// O spring cria e gerencia automaticamente um objeto desta classe
public class ClienteService {

    // declara os repositories que serão usados nesta classe
    //final impede que essa referencia sejam alteradas depois da inicialização

    private final ClienteRepository clienteRepo;
    private final EquipamentoRepository equipRepo;

    public ClienteService(ClienteRepository clienteRepo, EquipamentoRepository equipRepo){

        //Guarda o repositorio de clientes no atributo da classe

        this.clienteRepo = clienteRepo;

        this.equipRepo = equipRepo;

    }

    //Metodo responsavel por excluir o cliente pelo o seu ID
    public void exluir(Long id){

        if(equipRepo.existsByClienteId(id)) {
            throw new RegraDeNegocioException("Este cliente tem equipamentos");
        }

        clienteRepo.deleteById(id);
    }
}
