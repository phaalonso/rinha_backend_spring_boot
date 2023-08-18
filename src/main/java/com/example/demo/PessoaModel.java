package com.example.demo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PessoaModel {
    private UUID id;
    private String nome;
    private String apelido;
    private String nascimento;
    private List<String> stack;

    public PessoaModel() {
    }

    public PessoaModel(UUID id, String nome, String apelido, String nascimento, List<String> stack) {
        this.id = id;
        this.nome = nome;
        this.apelido = apelido;
        this.nascimento = nascimento;
        this.stack = stack;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public List<String> getStack() {
        return stack;
    }

    public void setStack(List<String> stack) {
        this.stack = stack;
    }

    @Override
    public String toString() {
        return "PessoaModel{" +
               "id=" + id +
               ", name='" + nome + '\'' +
               ", nick='" + apelido + '\'' +
               ", birthDate=" + nascimento +
               ", stack=" + stack +
               '}';
    }
}
