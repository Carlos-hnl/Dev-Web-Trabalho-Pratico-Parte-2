package com.taskmanager.controllers;

import com.taskmanager.dao.UsuarioDAO;
import com.taskmanager.model.Usuario;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet responsável pelo Cadastro de novos usuários.
 * Camada: Controller (MVC)
 *
 * GET  /cadastro → exibe o formulário
 * POST /cadastro → processa o cadastro
 */
@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String nome  = req.getParameter("nome");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        // ── Validações server-side ─────────────────────────────
        if (nome == null || nome.trim().length() < 3) {
            req.setAttribute("erro", "Nome deve ter ao menos 3 caracteres.");
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
            return;
        }

        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
            req.setAttribute("erro", "E-mail inválido.");
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
            return;
        }

        if (senha == null || senha.length() < 6) {
            req.setAttribute("erro", "Senha deve ter ao menos 6 caracteres.");
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
            return;
        }

        try {
            if (usuarioDAO.emailExiste(email)) {
                req.setAttribute("erro", "Este e-mail já está cadastrado.");
                req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
                return;
            }

            Usuario novoUsuario = new Usuario(nome.trim(), email.trim(), senha);
            boolean criado = usuarioDAO.criar(novoUsuario);

            if (criado) {
                // Redireciona para login com mensagem de sucesso
                resp.sendRedirect(req.getContextPath() + "/login?cadastro=ok");
            } else {
                req.setAttribute("erro", "Erro ao cadastrar. Tente novamente.");
                req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            getServletContext().log("Erro no cadastro", e);
            req.setAttribute("erro", "Erro interno. Tente novamente.");
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
        }
    }
}