package com.taskmanager.dao;

import com.taskmanager.model.Tarefa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pelas operações de banco relacionadas à entidade Tarefa.
 * Camada: DAO (Model)
 */
public class TarefaDAO {

    // ── Criar ─────────────────────────────────────────────────
    public boolean criar(Tarefa tarefa) throws SQLException {
        String sql = "INSERT INTO tarefas (usuario_id, titulo, descricao) VALUES (?, ?, ?)";

        try (Connection con = ConexaoPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tarefa.getUsuarioId());
            ps.setString(2, tarefa.getTitulo().trim());
            ps.setString(3, tarefa.getDescricao() != null ? tarefa.getDescricao().trim() : "");

            return ps.executeUpdate() > 0;
        }
    }

    // ── Listar por usuário ────────────────────────────────────
    public List<Tarefa> listarPorUsuario(int usuarioId) throws SQLException {
        String sql = """
                SELECT id, usuario_id, titulo, descricao, concluida, criado_em, atualizado_em
                FROM tarefas
                WHERE usuario_id = ?
                ORDER BY concluida ASC, criado_em DESC
                """;

        List<Tarefa> lista = new ArrayList<>();

        try (Connection con = ConexaoPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // ── Buscar por ID ─────────────────────────────────────────
    public Tarefa buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT id, usuario_id, titulo, descricao, concluida, criado_em, atualizado_em
                FROM tarefas WHERE id = ?
                """;

        try (Connection con = ConexaoPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ── Atualizar ─────────────────────────────────────────────
    public boolean atualizar(Tarefa tarefa) throws SQLException {
        String sql = "UPDATE tarefas SET titulo = ?, descricao = ? WHERE id = ? AND usuario_id = ?";

        try (Connection con = ConexaoPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tarefa.getTitulo().trim());
            ps.setString(2, tarefa.getDescricao() != null ? tarefa.getDescricao().trim() : "");
            ps.setInt(3, tarefa.getId());
            ps.setInt(4, tarefa.getUsuarioId());

            return ps.executeUpdate() > 0;
        }
    }

    // ── Alternar conclusão ────────────────────────────────────
    public boolean alternarConclusao(int tarefaId, int usuarioId) throws SQLException {
        String sql = """
                UPDATE tarefas
                SET concluida = NOT concluida
                WHERE id = ? AND usuario_id = ?
                """;

        try (Connection con = ConexaoPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tarefaId);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Excluir ───────────────────────────────────────────────
    public boolean excluir(int tarefaId, int usuarioId) throws SQLException {
        // usuarioId garante que o usuário só exclui suas próprias tarefas
        String sql = "DELETE FROM tarefas WHERE id = ? AND usuario_id = ?";

        try (Connection con = ConexaoPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tarefaId);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Mapeamento ResultSet → Tarefa ─────────────────────────
    private Tarefa mapear(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setId(rs.getInt("id"));
        t.setUsuarioId(rs.getInt("usuario_id"));
        t.setTitulo(rs.getString("titulo"));
        t.setDescricao(rs.getString("descricao"));
        t.setConcluida(rs.getBoolean("concluida"));

        Timestamp criado = rs.getTimestamp("criado_em");
        if (criado != null) t.setCriadoEm(criado.toLocalDateTime());

        Timestamp atualizado = rs.getTimestamp("atualizado_em");
        if (atualizado != null) t.setAtualizadoEm(atualizado.toLocalDateTime());

        return t;
    }
}
