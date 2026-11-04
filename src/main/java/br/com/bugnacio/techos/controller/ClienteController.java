package br.com.bugnacio.techos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.bugnacio.techos.repository.ClienteRepository;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository repo;

    public ClienteController(ClienteRepository repo){
        this.repo = repo;
    }

    @GetMapping()
    public String inicio(Model model) {

        model.addAttribute("clientes", repo.findAll());
        return "clientes/lista";
    }
}
