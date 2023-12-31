package com.example.demo;

import com.example.demo.exceptions.InvalidPeopleDataException;
import com.example.demo.exceptions.QueryParameterNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class PessoaController {
    private final PessoaRepository pessoaRepository;
    private final RedisTemplate redisTemplate;

    @Autowired
    public PessoaController(PessoaRepository pessoaRepository, RedisTemplate redisTemplate) {
        this.pessoaRepository = pessoaRepository;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/contagem-pessoas")
    public ResponseEntity<Integer> contagemPessoa() {
        var result = this.pessoaRepository.count();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/pessoas")
    @Transactional
    public ResponseEntity createPeople(@RequestBody PessoaModel model) throws SQLException {
        if (model.getApelido() == null || model.getApelido().length() > 32) {
            throw new InvalidPeopleDataException("Nick deve ser preenchido com tamanho maior que 32 caracteres");
        }

        if (model.getNome() == null || model.getNome().length() > 100) {
            throw new InvalidPeopleDataException("Nome deve ser preenchido com tamanho maior que 100 caracteres");
        }

        if (model.getNascimento() == null || !isDataValida(model.getNascimento())) {
            throw new InvalidPeopleDataException("Data nascimento inválida");
        }

        if (model.getStack() != null) {
            if (model.getStack().isEmpty()) {
                throw new InvalidPeopleDataException("Informe um item da stack ou stack como nulo");
            }

            model.getStack().forEach(s -> {
                if (s.length() > 32) {
                    throw new InvalidPeopleDataException("Stack maior que 32 caracteres");
                }
            });
        }

        model.setId(UUID.randomUUID());

        pessoaRepository.inserePessoa(model);

        redisTemplate.opsForValue().set(model.getId().toString(), model);

        return ResponseEntity.created(
                        UriComponentsBuilder.fromPath("/pessoas/{id}")
                                .buildAndExpand(model.getId())
                                .toUri())
                .build();
    }

    @GetMapping("/pessoas")
    public ResponseEntity<List<PessoaModel>> findBessoa(@RequestParam(required = false) String t) {
        if (t == null || t.isEmpty() || t.isBlank()) {
            throw new QueryParameterNotFound();
        }

        var result = this.pessoaRepository.findByTermo(t);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/pessoas/{id}")
    public ResponseEntity<PessoaModel> getPessoaById(@PathVariable String id) {
        PessoaModel result = (PessoaModel) redisTemplate.opsForValue().get(id);

        if (result == null) {
           result = this.pessoaRepository.findById(id);
        }

        return ResponseEntity.ok(result);
    }

    boolean isDataValida(String dataNascimento) {
        try {
            LocalDate.parse(dataNascimento);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
