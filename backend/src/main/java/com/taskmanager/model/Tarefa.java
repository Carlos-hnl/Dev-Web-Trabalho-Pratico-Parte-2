package com.taskmanager.model;

import java.time.LocalDateTime;

/**
 * JavaBean que representa uma Tarefa do usuário.
 * Camada: Model (MVC)
 */
public class Tarefa {

    private int id;
    private int usuarioId;
    private String titulo;
    private String descricao;
    private boolean concluida;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Tarefa() {}

    public Tarefa(int usuarioId, String titulo, String descricao) {
        this.usuarioId = usuarioId;
        this.titulo    = titulo;
        this.descricao = descricao;
        this.concluida = false;
    }

    // ── Regra de Negócio ──────────────────────────────────────
    /**
     * Alterna o estado de conclusão da tarefa.
     */
    public void alternarConclusao() {
        this.concluida = !this.concluida;
    }

    // ── Getters & Setters ──────────────────────────────────────
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public int getUsuarioId()                       { return usuarioId; }
    public void setUsuarioId(int usuarioId)         { this.usuarioId = usuarioId; }

    public String getTitulo()                       { return titulo; }
    public void setTitulo(String titulo)            { this.titulo = titulo; }

    public String getDescricao()                    { return descricao; }
    public void setDescricao(String descricao)      { this.descricao = descricao; }

    public boolean isConcluida()                    { return concluida; }
    public void setConcluida(boolean concluida)     { this.concluida = concluida; }

    public LocalDateTime getCriadoEm()                      { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm)         { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm()                  { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    @Override
    public String toString() {
        return "Tarefa{id=" + id + ", titulo='" + titulo + "', concluida=" + concluida + "}";
    }
}
