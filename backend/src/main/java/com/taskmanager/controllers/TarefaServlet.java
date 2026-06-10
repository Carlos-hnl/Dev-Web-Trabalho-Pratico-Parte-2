package com.taskmanager.controller;

import com.taskmanager.dao.TarefaDAO;
import com.taskmanager.model.Tarefa;
import com.taskmanager.model.Usuario;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet responsável por todo o CRUD de Tarefas.
 * Camada: Controller (MVC)
 *
 * GET  /dashboard          → lista tarefas do usuário logado
 * POST /tarefa?acao=criar   → cria nova tarefa
 * POST /tarefa?acao=editar  → atualiza tarefa existente
 * POST /tarefa?acao=concluir→ alterna status de conclusão
 * POST /tarefa?acao=excluir → exclui tarefa
 */
@WebServlet({"/dashboard", "/tarefa"})
public class TarefaServlet extends HttpServlet {

    private final TarefaDAO tarefaDAO = new TarefaDAO();

    // ── GET: exibir dashboard com lista de tarefas ─────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Cabeçalhos para evitar cache em páginas protegidas
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        Usuario usuario = getUsuarioSessao(req, resp);
        if (usuario == null) return; // AuthFilter já redireciona

        try {
            List<Tarefa> tarefas = tarefaDAO.listarPorUsuario(usuario.getId());
            req.setAttribute("tarefas", tarefas);
            req.setAttribute("usuario", usuario);

            // Contadores para o dashboard
            long concluidas = tarefas.stream().filter(Tarefa::isConcluida).count();
            req.setAttribute("totalTarefas",    tarefas.size());
            req.setAttribute("tarefasConcluidas", concluidas);
            req.setAttribute("tarefasPendentes", tarefas.size() - concluidas);

            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);

        } catch (SQLException e) {
            getServletContext().log("Erro ao listar tarefas", e);
            req.setAttribute("erro", "Erro ao carregar tarefas.");
            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
        }
    }

    // ── POST: processar ações CRUD ─────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        Usuario usuario = getUsuarioSessao(req, resp);
        if (usuario == null) return;

        String acao = req.getParameter("acao");

        try {
            switch (acao == null ? "" : acao) {

                case "criar" -> {
                    String titulo    = req.getParameter("titulo");
                    String descricao = req.getParameter("descricao");

                    if (titulo == null || titulo.isBlank()) {
                        redirectComMensagem(req, resp, null, "Título é obrigatório.");
                        return;
                    }

                    Tarefa nova = new Tarefa(usuario.getId(), titulo, descricao);
                    tarefaDAO.criar(nova);
                    redirectComMensagem(req, resp, "Tarefa criada com sucesso!", null);
                }

                case "editar" -> {
                    int    id        = Integer.parseInt(req.getParameter("id"));
                    String titulo    = req.getParameter("titulo");
                    String descricao = req.getParameter("descricao");

                    if (titulo == null || titulo.isBlank()) {
                        redirectComMensagem(req, resp, null, "Título é obrigatório.");
                        return;
                    }

                    Tarefa tarefa = new Tarefa();
                    tarefa.setId(id);
                    tarefa.setUsuarioId(usuario.getId());
                    tarefa.setTitulo(titulo);
                    tarefa.setDescricao(descricao);

                    tarefaDAO.atualizar(tarefa);
                    redirectComMensagem(req, resp, "Tarefa atualizada!", null);
                }

                case "concluir" -> {
                    int id = Integer.parseInt(req.getParameter("id"));
                    tarefaDAO.alternarConclusao(id, usuario.getId());
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                }

                case "excluir" -> {
                    int id = Integer.parseInt(req.getParameter("id"));
                    tarefaDAO.excluir(id, usuario.getId());
                    redirectComMensagem(req, resp, "Tarefa excluída.", null);
                }

                default -> resp.sendRedirect(req.getContextPath() + "/dashboard");
            }

        } catch (SQLException | NumberFormatException e) {
            getServletContext().log("Erro na operação de tarefa", e);
            redirectComMensagem(req, resp, null, "Erro interno. Tente novamente.");
        }
    }

    // ── Utilitários ────────────────────────────────────────────

    private Usuario getUsuarioSessao(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return (Usuario) session.getAttribute("usuarioLogado");
    }

    private void redirectComMensagem(HttpServletRequest req, HttpServletResponse resp,
                                     String sucesso, String erro) throws IOException {
        StringBuilder url = new StringBuilder(req.getContextPath() + "/dashboard");
        if (sucesso != null) {
            url.append("?msg=").append(encode(sucesso));
        } else if (erro != null) {
            url.append("?erro=").append(encode(erro));
        }
        resp.sendRedirect(url.toString());
    }

    private String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
}
