package br.com.bugnacio.techos.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.bugnacio.techos.model.Cliente;
import br.com.bugnacio.techos.model.Equipamento;
import br.com.bugnacio.techos.model.TipoEquipamento;
import br.com.bugnacio.techos.repository.ClienteRepository;
import br.com.bugnacio.techos.repository.EquipamentoRepository;
import jakarta.validation.Valid;

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
                repo.findByMarcaContainingIgnoreCaseOrModeloContainingIgnoreCase(
                        busca, busca));
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

    @PostMapping("/salvar")
    public String salvar(
            @Valid Equipamento equipamento,
            BindingResult result,
            @RequestParam("cliente_id") Long cliente_id,
            Model model,
            RedirectAttributes redirect) {

        Cliente cliente = clienteRepo.findById(cliente_id)
                .orElseThrow();

        equipamento.setCliente(cliente);

        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteRepo.findAll());
            model.addAttribute("tipos", TipoEquipamento.values());

            return "equipamentos/form";
        }

        boolean edicao = equipamento.getId() != null;

        repo.save(equipamento);

        if (edicao) {
            redirect.addFlashAttribute(
                    "update", "Equipamento atualizado com sucesso!");
        } else {
            redirect.addFlashAttribute(
                    "mensagem", "Equipamento cadastrado com sucesso!");
        }

        return "redirect:/equipamentos";
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model) {

        Equipamento equipamento = repo.findById(id)
                .orElseThrow();

        model.addAttribute("equipamento", equipamento);
        model.addAttribute("clientes", clienteRepo.findAll());
        model.addAttribute("tipos", TipoEquipamento.values());

        return "equipamentos/form";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        repo.deleteById(id);

        redirect.addFlashAttribute(
                "erro",
                "Equipamento excluído com sucesso!");

        return "redirect:/equipamentos";
    }

}
