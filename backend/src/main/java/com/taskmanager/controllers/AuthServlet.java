package com.taskmanager.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taskmanager.dao.UsuarioDAO;
import com.taskmanager.model.Usuario;
import com.taskmanager.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 * API REST de Autenticação.
 * Camada: Controller
 *
 * POST /api/auth/login    → autentica usuário, cria sessão
 * POST /api/auth/cadastro → cadastra novo usuário
 * POST /api/auth/logout   → encerra sessão
 * GET  /api/auth/me       → retorna dados do usuário logado
 */
@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ── GET /api/auth/me ─────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String acao = getAcao(req);

        if ("me".equals(acao)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("usuarioLogado") == null) {
                JsonUtil.enviar(resp, 401, JsonUtil.erro("Não autenticado."));
                return;
            }
            Usuario u = (Usuario) session.getAttribute("usuarioLogado");
            // Nunca retornar a senha
            JsonObject dados = new JsonObject();
            dados.addProperty("id",    u.getId());
            dados.addProperty("nome",  u.getNome());
            dados.addProperty("email", u.getEmail());
            JsonUtil.enviar(resp, 200, JsonUtil.sucesso(dados));
        } else {
            JsonUtil.enviar(resp, 404, JsonUtil.erro("Rota não encontrada."));
        }
    }

    // ── POST /api/auth/* ─────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String acao = getAcao(req);
        JsonObject body = lerBody(req);

        switch (acao) {
            case "login"    -> handleLogin(req, resp, body);
            case "cadastro" -> handleCadastro(resp, body);
            case "logout"   -> handleLogout(req, resp);
            default         -> JsonUtil.enviar(resp, 404, JsonUtil.erro("Rota não encontrada."));
        }
    }

    // ── Login ─────────────────────────────────────────────────
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp, JsonObject body)
            throws IOException {
        String email = getStr(body, "email");
        String senha = getStr(body, "senha");

        if (email.isBlank() || senha.isBlank()) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("E-mail e senha são obrigatórios."));
            return;
        }

        try {
            Usuario usuario = usuarioDAO.autenticar(email, senha);
            if (usuario == null) {
                JsonUtil.enviar(resp, 401, JsonUtil.erro("E-mail ou senha incorretos."));
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("usuarioLogado", usuario);
            session.setMaxInactiveInterval(30 * 60); // 30 minutos

            resp.addHeader("Set-Cookie",
                    "JSESSIONID=" + session.getId()
                            + "; Path=" + req.getContextPath() + "/"
                            + "; Max-Age=" + (30 * 60)
                            + "; HttpOnly"
                            + "; SameSite=None"
                            + "; Secure");

            JsonObject dados = new JsonObject();
            dados.addProperty("id",    usuario.getId());
            dados.addProperty("nome",  usuario.getNome());
            dados.addProperty("email", usuario.getEmail());

            JsonUtil.enviar(resp, 200, JsonUtil.sucesso(dados));

        } catch (SQLException e) {
            getServletContext().log("Erro no login", e);
            JsonUtil.enviar(resp, 500, JsonUtil.erro("Erro interno. Tente novamente."));
        }
    }

    // ── Cadastro ──────────────────────────────────────────────
    private void handleCadastro(HttpServletResponse resp, JsonObject body)
            throws IOException {
        String nome  = getStr(body, "nome");
        String email = getStr(body, "email");
        String senha = getStr(body, "senha");

        if (nome.trim().length() < 3) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("Nome deve ter ao menos 3 caracteres."));
            return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("E-mail inválido."));
            return;
        }
        if (senha.length() < 6) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("Senha deve ter ao menos 6 caracteres."));
            return;
        }

        try {
            if (usuarioDAO.emailExiste(email)) {
                JsonUtil.enviar(resp, 409, JsonUtil.erro("Este e-mail já está cadastrado."));
                return;
            }

            Usuario novo = new Usuario(nome.trim(), email.trim(), senha);
            boolean criado = usuarioDAO.criar(novo);

            if (criado) {
                JsonUtil.enviar(resp, 201, JsonUtil.sucesso("Cadastro realizado! Faça login."));
            } else {
                JsonUtil.enviar(resp, 500, JsonUtil.erro("Erro ao cadastrar. Tente novamente."));
            }

        } catch (SQLException e) {
            getServletContext().log("Erro no cadastro", e);
            JsonUtil.enviar(resp, 500, JsonUtil.erro("Erro interno. Tente novamente."));
        }
    }

    // ── Logout ────────────────────────────────────────────────
    private void handleLogout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
                
        resp.addHeader("Set-Cookie",
                "JSESSIONID=; Path=" + req.getContextPath() + "/"
                        + "; Max-Age=0"
                        + "; HttpOnly"
                        + "; SameSite=None"
                        + "; Secure");

        JsonUtil.enviar(resp, 200, JsonUtil.sucesso("Logout realizado com sucesso."));
    }

    // ── Utilitários ───────────────────────────────────────────
    private String getAcao(HttpServletRequest req) {
        String info = req.getPathInfo();
        if (info == null || info.equals("/")) return "";
        return info.substring(1).toLowerCase();
    }

    private JsonObject lerBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        String json = sb.toString().trim();
        if (json.isEmpty()) return new JsonObject();
        try {
            return JsonParser.parseString(json).getAsJsonObject();
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    private String getStr(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString().trim();
        }
        return "";
    }
}
