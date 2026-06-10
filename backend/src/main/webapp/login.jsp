<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TaskManager — Login</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="auth-body">

<div class="auth-wrapper">

    <div class="auth-logo">
        <div class="mark">T</div>
        <h1>TaskManager</h1>
        <p>Organize suas tarefas com simplicidade</p>
    </div>

    <div class="auth-card">
        <h2 class="auth-card-title">Entrar na conta</h2>

        <% if ("ok".equals(request.getParameter("cadastro"))) { %>
            <div class="alert alert-success">Conta criada com sucesso. Faça login.</div>
        <% } %>
        <% if ("ok".equals(request.getParameter("logout"))) { %>
            <div class="alert alert-info">Você saiu da sua conta.</div>
        <% } %>
        <% if (request.getAttribute("erro") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("erro") %></div>
        <% } %>

        <form action="login" method="post" onsubmit="return validarLogin()">

            <div class="form-group">
                <label for="email">E-mail</label>
                <input type="email" name="email" id="email" placeholder="seu@email.com"
                       value="<%= request.getAttribute("emailSalvo") != null ? request.getAttribute("emailSalvo") : "" %>"
                       autocomplete="email" required>
            </div>

            <div class="form-group">
                <label for="senha">Senha</label>
                <div class="input-wrap">
                    <input type="password" name="senha" id="senha"
                           placeholder="••••••••" autocomplete="current-password" required>
                    <button type="button" class="btn-eye" onclick="toggleSenha('senha', this)">👁</button>
                </div>
            </div>

            <label class="form-check">
                <input type="checkbox" name="lembrar">
                <span>Lembrar meu e-mail</span>
            </label>

            <button type="submit" class="btn btn-primary">Entrar</button>
        </form>

        <div class="auth-card-footer">
            Não tem conta? <a href="cadastro">Criar conta</a>
        </div>
    </div>

</div>

<script src="js/script.js"></script>
</body>
</html>
