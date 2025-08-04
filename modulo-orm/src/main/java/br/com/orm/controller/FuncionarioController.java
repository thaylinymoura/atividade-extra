package br.com.orm.controller;

import br.com.orm.model.Funcionario;
import br.com.orm.repository.FuncionarioRepository;
import br.com.orm.service.FuncionarioProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private FuncionarioProducerService producerService;

    @GetMapping
    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    @PostMapping
    public Funcionario criarFuncionario(@RequestBody Funcionario funcionario) {
        Funcionario novoFuncionario = funcionarioRepository.save(funcionario);
        producerService.enviarFuncionario(novoFuncionario);
        return novoFuncionario;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizarFuncionario(@PathVariable Long id, @RequestBody Funcionario funcionarioDetails) {
        return funcionarioRepository.findById(id)
                .map(funcionario -> {
                    funcionario.setNome(funcionarioDetails.getNome());
                    funcionario.setDataDeNascimento(funcionarioDetails.getDataDeNascimento());
                    funcionario.setSalario(funcionarioDetails.getSalario());
                    Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);
                    producerService.enviarFuncionario(funcionarioAtualizado);
                    return ResponseEntity.ok(funcionarioAtualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        return funcionarioRepository.findById(id)
                .map(funcionario -> {
                    funcionarioRepository.delete(funcionario);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
