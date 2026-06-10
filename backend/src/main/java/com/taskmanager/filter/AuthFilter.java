package com.taskmanager.filter;

import com.taskmanager.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Filtro de Autenticação via Sessão para as rotas protegidas da API.
 * Rotas protegidas: /api/tarefas/*, /api/usuario/*
 * Se não houver sessão válida, retorna 401 Unauthorized em JSON.
 */
@WebFilter({"/tarefas/*", "/usuario/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Preflight OPTIONS passa sempre (tratado no CorsFilter)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean logado = session != null && session.getAttribute("usuarioLogado") != null;

        if (logado) {
            chain.doFilter(req, res);
        } else {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                JsonUtil.erro("Não autenticado. Faça login para continuar.")
            );
        }
    }

    @Override public void init(FilterConfig cfg) {}
    @Override public void destroy() {}
}
