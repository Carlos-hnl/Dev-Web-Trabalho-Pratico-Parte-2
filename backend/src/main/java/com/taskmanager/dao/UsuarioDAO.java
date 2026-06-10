package com.taskmanager.dao;

import com.taskmanager.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

/**
 * DAO (Data Access Object) responsável pelas operações de banco
 * relacionadas à entidade Usuario.
 * Camada: DAO (Model)
 */
public class UsuarioDAO {

    // ── Criar ─────────────────────────────────────────────────
    /**
     * Insere um novo usuário no banco após fazer hash da senha com BCrypt.
     * @return true se inserido com sucesso
     */
    public boolean criar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";

        // Hash da senha antes de persistir
        String senhaHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt(12));

        try (Connection con = ConexaoPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail().toLowerCase().trim());
            ps.setString(3, senhaHash);

            return ps.executeUpdate() > 0;
        }
    }

    // ── Buscar por email ──────────────────────────────────────
    /**
     * Busca um usuário pelo e-mail. Retorna null se não encontrado.
     */
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT id, nome, email, senha, criado_em FROM usuarios WHERE email = ?";

        try (Connection con = ConexaoPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.toLowerCase().trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    // ── Autenticar ────────────────────────────────────────────
    /**
     * Verifica credenciais. Retorna o Usuario autenticado ou null.
     */
    public Usuario autenticar(String email, String senhaDigitada) throws SQLException {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null) return null;

        // Verifica hash BCrypt
        if (BCrypt.checkpw(senhaDigitada, usuario.getSenha())) {
            return usuario;
        }
        return null;
    }

    // ── Verificar e-mail duplicado ────────────────────────────
    public boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";

        try (Connection con = ConexaoPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.toLowerCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ── Mapeamento ResultSet → Usuario ────────────────────────
    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenha(rs.getString("senha"));
        Timestamp ts = rs.getTimestamp("criado_em");
        if (ts != null) u.setCriadoEm(ts.toLocalDateTime());
        return u;
    }
}
