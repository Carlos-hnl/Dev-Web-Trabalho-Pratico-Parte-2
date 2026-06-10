package com.taskmanager.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Filtro de Autenticação — intercepta TODAS as requisições e verifica
 * se o usuário possui sessão ativa antes de acessar páginas protegidas.
 * Camada: Segurança (Filter)
 *
 * Rotas públicas (sem autenticação): /login, /cadastro, /css/*, /js/*, /img/*
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    // Caminhos que não exigem autenticação
    private static final Set<String> ROTAS_PUBLICAS = new HashSet<>(Arrays.asList(
            "/login", "/cadastro"
    ));

    // Prefixos de recursos estáticos
    private static final String[] PREFIXOS_PUBLICOS = {
            "/css/", "/js/", "/img/", "/favicon.ico"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String caminho = req.getServletPath();

        // ── Libera recursos estáticos e rotas públicas ─────────
        if (ehPublico(caminho)) {
            chain.doFilter(request, response);
            return;
        }

        // ── Verifica sessão ────────────────────────────────────
        HttpSession session = req.getSession(false);
        boolean logado = session != null && session.getAttribute("usuarioLogado") != null;

        if (logado) {
            chain.doFilter(request, response); // continua normalmente
        } else {
            // Não autenticado → redireciona para login
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    private boolean ehPublico(String caminho) {
        if (ROTAS_PUBLICAS.contains(caminho)) return true;
        for (String prefixo : PREFIXOS_PUBLICOS) {
            if (caminho.startsWith(prefixo)) return true;
        }
        return false;
    }

    @Override public void init(FilterConfig cfg) {}
    @Override public void destroy() {}
    }
