package com.taskmanager.model;

import java.time.LocalDateTime;

/**
 * JavaBean que representa um Usuário do sistema.
 * Camada: Model (MVC)
 */
public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String senha;           // armazena o hash BCrypt
    private LocalDateTime criadoEm;

    public Usuario() {}

    public Usuario(String nome, String email, String senha) {
        this.nome  = nome;
        this.email = email;
        this.senha = senha;
    }

    // ── Getters & Setters ──────────────────────────────────────
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getNome()                 { return nome; }
    public void setNome(String nome)        { this.nome = nome; }

    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }

    public String getSenha()                { return senha; }
    public void setSenha(String senha)      { this.senha = senha; }

    public LocalDateTime getCriadoEm()              { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email + "'}";
    }
}
