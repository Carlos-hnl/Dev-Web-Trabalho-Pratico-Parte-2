package com.taskmanager.controllers;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Servlet responsável pelo Logout.
 * Invalida a sessão e redireciona para o login.
 * Camada: Controller (MVC)
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        executarLogout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        executarLogout(req, resp);
    }

    private void executarLogout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Invalida a sessão HTTP no servidor
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Previne cache da resposta
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        resp.sendRedirect(req.getContextPath() + "/login?logout=ok");
    }
}