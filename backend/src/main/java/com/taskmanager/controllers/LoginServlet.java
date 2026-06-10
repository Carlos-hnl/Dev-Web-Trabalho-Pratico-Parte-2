package com.taskmanager.controller;

import com.taskmanager.dao.UsuarioDAO;
import com.taskmanager.model.Usuario;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet responsável pelo processo de Login.
 * Camada: Controller (MVC)
 *
 * GET  /login  → exibe a página de login
 * POST /login  → processa as credenciais
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ── GET: exibir tela de login ──────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Cabeçalhos anti-cache para páginas dinâmicas/sensíveis
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        // Se já estiver logado, redireciona direto para o dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuarioLogado") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        // Verifica cookie "lembrarEmail" para pré-preencher
        String emailSalvo = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("lembrarEmail".equals(c.getName())) {
                    emailSalvo = c.getValue();
                }
            }
        }

        req.setAttribute("emailSalvo", emailSalvo);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    // ── POST: processar credenciais ────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String senha = req.getParameter("senha");
        String lembrar = req.getParameter("lembrar"); // "on" ou null

        // Validação básica server-side (defesa em profundidade)
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            req.setAttribute("erro", "Preencha todos os campos.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.autenticar(email, senha);

            if (usuario == null) {
                req.setAttribute("erro", "E-mail ou senha incorretos.");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            // ── Sessão: persiste usuário logado ────────────────
            HttpSession session = req.getSession(true);
            session.setAttribute("usuarioLogado", usuario);
            session.setMaxInactiveInterval(30 * 60); // 30 minutos

            // ── Cookie: lembrar e-mail ─────────────────────────
            if ("on".equals(lembrar)) {
                Cookie cookieEmail = new Cookie("lembrarEmail", email);
                cookieEmail.setMaxAge(30 * 24 * 60 * 60); // 30 dias
                cookieEmail.setPath(req.getContextPath() + "/");
                cookieEmail.setHttpOnly(true);
                resp.addCookie(cookieEmail);
            } else {
                // Remove o cookie se desmarcou "lembrar"
                Cookie cookieEmail = new Cookie("lembrarEmail", "");
                cookieEmail.setMaxAge(0);
                cookieEmail.setPath(req.getContextPath() + "/");
                resp.addCookie(cookieEmail);
            }

            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (SQLException e) {
            getServletContext().log("Erro no login", e);
            req.setAttribute("erro", "Erro interno. Tente novamente.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
