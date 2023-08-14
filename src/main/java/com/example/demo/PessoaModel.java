package com.example.demo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PessoaModel {
    private UUID id;
    private String name;
    private String nick;
    private LocalDate birthDate;
    private List<String> stack;

    public PessoaModel() {
    }

    public PessoaModel(UUID id, String name, String nick, LocalDate birthDate, List<String> stack) {
        this.id = id;
        this.name = name;
        this.nick = nick;
        this.birthDate = birthDate;
        this.stack = stack;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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
                ", name='" + name + '\'' +
                ", nick='" + nick + '\'' +
                ", birthDate=" + birthDate +
                ", stack=" + stack +
                '}';
    }
}
