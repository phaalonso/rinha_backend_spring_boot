package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@RestController
public class PessoaController {
    private final PessoaRepository pessoaRepository;

    @Autowired
    public PessoaController(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @GetMapping("/contagem-pessoas")
    public ResponseEntity<Integer> contagemPessoa() {
        var result = this.pessoaRepository.count();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/pessoas")
    @Transactional
    public ResponseEntity createPeople(@RequestBody PessoaModel model) throws SQLException {
        if (model.getNick().length() > 32) {
            throw new RuntimeException("Nick com tamanho maior que 32");
        }

        if (model.getName().length() > 100) {
            throw new RuntimeException("Nome com tamanho maior que 100");
        }

        model.setId(UUID.randomUUID());

        pessoaRepository.inserePessoa(model);

        return ResponseEntity.created(UriComponentsBuilder.fromPath("/people").path("/{id}").buildAndExpand(model.getId()).toUri()).build();

    }

    @GetMapping("/pessoas")
    public ResponseEntity<List<PessoaModel>> findBessoa(@RequestParam String t) {
        var result = this.pessoaRepository.findByTermo(t);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/pessoas/{id}")
    public ResponseEntity<PessoaModel> getPessoaById(@PathVariable String id) {
        var result = this.pessoaRepository.findById(id);

        return ResponseEntity.ok(result);
    }

}
