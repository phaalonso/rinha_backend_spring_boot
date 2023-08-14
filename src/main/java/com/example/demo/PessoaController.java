package com.example.demo;

import akka.pattern.StatusReply;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.SQLException;
import java.util.UUID;

@RestController
@RequestMapping("/people")
public class PessoaController {
//    @GetMapping("health")
//    public String getHealth() {
//        return "Ok";
//    }
    private final PessoaRepository pessoaRepository;

    @Autowired
    public PessoaController(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @PostMapping
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

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(model.getId()).toUri()).build();

    }

    @GetMapping("/:id")
    public ResponseEntity<PessoaModel> getPessoaById(@PathParam("id") String id) {
        var result = this.pessoaRepository.findById(id);

        return ResponseEntity.ok(result);
    }
}
