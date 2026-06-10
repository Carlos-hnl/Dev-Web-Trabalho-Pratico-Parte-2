package com.taskmanager.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taskmanager.dao.TarefaDAO;
import com.taskmanager.model.Tarefa;
import com.taskmanager.model.Usuario;
import com.taskmanager.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * API REST de Tarefas — CRUD completo via JSON.
 * Camada: Controller
 *
 * GET    /api/tarefas        → lista todas as tarefas do usuário logado
 * POST   /api/tarefas        → cria nova tarefa
 * PUT    /api/tarefas/{id}   → atualiza título/descrição
 * DELETE /api/tarefas/{id}   → exclui tarefa
 * PATCH  /api/tarefas/{id}/concluir → alterna status de conclusão
 */
@WebServlet("/tarefas/*")
public class TarefaServlet extends HttpServlet {

    private final TarefaDAO tarefaDAO = new TarefaDAO();

    // ── GET /api/tarefas ─────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Usuario usuario = getUsuarioSessao(req);

        try {
            List<Tarefa> tarefas = tarefaDAO.listarPorUsuario(usuario.getId());
            JsonUtil.enviar(resp, 200, JsonUtil.sucesso(tarefas));
        } catch (SQLException e) {
            getServletContext().log("Erro ao listar tarefas", e);
            JsonUtil.enviar(resp, 500, JsonUtil.erro("Erro ao carregar tarefas."));
        }
    }

    // ── POST /api/tarefas ────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Usuario usuario = getUsuarioSessao(req);
        JsonObject body = lerBody(req);

        String titulo    = getStr(body, "titulo");
        String descricao = getStr(body, "descricao");

        if (titulo.isBlank()) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("Título é obrigatório."));
            return;
        }

        try {
            Tarefa nova = new Tarefa(usuario.getId(), titulo, descricao);
            tarefaDAO.criar(nova);
            JsonUtil.enviar(resp, 201, JsonUtil.sucesso("Tarefa criada com sucesso!"));
        } catch (SQLException e) {
            getServletContext().log("Erro ao criar tarefa", e);
            JsonUtil.enviar(resp, 500, JsonUtil.erro("Erro ao criar tarefa."));
        }
    }

    // ── PUT /api/tarefas/{id} ─────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Usuario usuario = getUsuarioSessao(req);
        Integer id = extrairId(req);

        if (id == null) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("ID inválido."));
            return;
        }

        JsonObject body = lerBody(req);
        String titulo    = getStr(body, "titulo");
        String descricao = getStr(body, "descricao");

        if (titulo.isBlank()) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("Título é obrigatório."));
            return;
        }

        try {
            Tarefa tarefa = new Tarefa();
            tarefa.setId(id);
            tarefa.setUsuarioId(usuario.getId());
            tarefa.setTitulo(titulo);
            tarefa.setDescricao(descricao);

            boolean atualizado = tarefaDAO.atualizar(tarefa);

            if (atualizado) {
                JsonUtil.enviar(resp, 200, JsonUtil.sucesso("Tarefa atualizada!"));
            } else {
                JsonUtil.enviar(resp, 404, JsonUtil.erro("Tarefa não encontrada ou sem permissão."));
            }
        } catch (SQLException e) {
            getServletContext().log("Erro ao atualizar tarefa", e);
            JsonUtil.enviar(resp, 500, JsonUtil.erro("Erro ao atualizar tarefa."));
        }
    }

    // ── DELETE /api/tarefas/{id} ──────────────────────────────
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Usuario usuario = getUsuarioSessao(req);
        Integer id = extrairId(req);

        if (id == null) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("ID inválido."));
            return;
        }

        try {
            boolean excluido = tarefaDAO.excluir(id, usuario.getId());

            if (excluido) {
                JsonUtil.enviar(resp, 200, JsonUtil.sucesso("Tarefa excluída."));
            } else {
                JsonUtil.enviar(resp, 404, JsonUtil.erro("Tarefa não encontrada ou sem permissão."));
            }
        } catch (SQLException e) {
            getServletContext().log("Erro ao excluir tarefa", e);
            JsonUtil.enviar(resp, 500, JsonUtil.erro("Erro ao excluir tarefa."));
        }
    }

    /**
     * PATCH /api/tarefas/{id}/concluir
     * Alterna o status de conclusão da tarefa.
     * Implementado via service (workaround para PATCH com Servlet puro).
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            handlePatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    private void handlePatch(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Usuario usuario = getUsuarioSessao(req);
        String pathInfo = req.getPathInfo(); // /{id}/concluir

        if (pathInfo == null || !pathInfo.endsWith("/concluir")) {
            JsonUtil.enviar(resp, 404, JsonUtil.erro("Ação não reconhecida."));
            return;
        }

        String[] partes = pathInfo.split("/");
        // pathInfo = /123/concluir → partes[1]=123
        if (partes.length < 2) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("ID inválido."));
            return;
        }

        try {
            int id = Integer.parseInt(partes[1]);
            boolean ok = tarefaDAO.alternarConclusao(id, usuario.getId());

            if (ok) {
                JsonUtil.enviar(resp, 200, JsonUtil.sucesso("Status de conclusão alterado."));
            } else {
                JsonUtil.enviar(resp, 404, JsonUtil.erro("Tarefa não encontrada ou sem permissão."));
            }
        } catch (NumberFormatException e) {
            JsonUtil.enviar(resp, 400, JsonUtil.erro("ID inválido."));
        } catch (SQLException e) {
            getServletContext().log("Erro ao alternar conclusão", e);
            JsonUtil.enviar(resp, 500, JsonUtil.erro("Erro ao atualizar tarefa."));
        }
    }

    // ── Utilitários ───────────────────────────────────────────

    private Usuario getUsuarioSessao(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (Usuario) session.getAttribute("usuarioLogado");
    }

    private Integer extrairId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try {
            String[] partes = pathInfo.split("/");
            return Integer.parseInt(partes[1]);
        } catch (Exception e) {
            return null;
        }
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
