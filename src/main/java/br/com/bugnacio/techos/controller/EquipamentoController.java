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
            @RequestParam(value = "cliente_id", required = false) Long cliente_id,
            Model model,
            RedirectAttributes redirect) {

        boolean novoCadastro = equipamento.getId() == null;

        // 1. Validar se um cliente foi selecionado
        if (cliente_id == null) {
            result.rejectValue("cliente", "cliente.obrigatorio",
                    "Selecione um cliente.");
        } else {
            Cliente cliente = clienteRepo.findById(cliente_id).orElse(null);

            if (cliente == null) {
                result.rejectValue("cliente", "cliente.invalido",
                        "Cliente não encontrado.");
            } else {
                equipamento.setCliente(cliente);
            }
        }

        // 2. Verificar número de série duplicado
        if (equipamento.getNumeroSerie() != null
                && !equipamento.getNumeroSerie().isBlank()) {

            boolean jaExiste;

            if (novoCadastro) {
                jaExiste = repo.existsByNumeroSerie(equipamento.getNumeroSerie());
            } else {
                jaExiste = repo.existsByNumeroSerieAndIdNot(
                        equipamento.getNumeroSerie(),
                        equipamento.getId());
            }

            if (jaExiste) {
                result.rejectValue(
                        "numeroSerie",
                        "duplicado",
                        "Já existe um equipamento com esse número de série.");
            }
        }

        // 3. Se houver erros, retornar ao formulário
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteRepo.findAll());
            model.addAttribute("tipos", TipoEquipamento.values());

            return "equipamentos/form";
        }

        // 4. Salvar equipamento
        repo.save(equipamento);

        if (novoCadastro) {
            redirect.addFlashAttribute(
                    "mensagem",
                    "Equipamento cadastrado com sucesso!");
        } else {
            redirect.addFlashAttribute(
                    "update",
                    "Equipamento atualizado com sucesso!");
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
