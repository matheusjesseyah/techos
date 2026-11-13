package br.com.bugnacio.techos.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.bugnacio.techos.model.Equipamento;
import br.com.bugnacio.techos.model.TipoEquipamento;
import br.com.bugnacio.techos.repository.ClienteRepository;
import br.com.bugnacio.techos.repository.EquipamentoRepository;

@Controller
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private final EquipamentoRepository repo;
    private final ClienteRepository clienteRepo;

    public EquipamentoController(
            EquipamentoRepository repo,
            ClienteRepository clienteRepo) {

        this.repo = repo;
        this.clienteRepo = clienteRepo;
    }

    // READ: lista (com busca opcional por nome e ordem alfabetica).
    @GetMapping
    public String listar(
            @RequestParam(value = "busca", required = false) String busca,
            Model model) {
        if (busca != null && !busca.isBlank()) {
            model.addAttribute(
                    "equipamentos",
                    repo.findByModeloContainingIgnoreCase(busca));
        } else {
            model.addAttribute(
                    "equipamentos",
                    repo.findAll(Sort.by("modelo")));
        }
        model.addAttribute("busca", busca);
        return "equipamentos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("equipamento", new Equipamento());
        // Buscar clientes cadastrados no banco
        model.addAttribute("clientes", clienteRepo.findAll());
        // Buscar opções do Enum
        model.addAttribute("tipos", TipoEquipamento.values());
        return "equipamentos/form";
    }
}
