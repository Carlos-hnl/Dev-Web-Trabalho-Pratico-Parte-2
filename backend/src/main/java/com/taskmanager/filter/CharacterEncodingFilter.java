package com.taskmanager.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Garante codificação UTF-8 em todas as requisições e respostas.
 */
@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        chain.doFilter(req, res);
    }

    @Override public void init(FilterConfig cfg) {}
    @Override public void destroy() {}
}