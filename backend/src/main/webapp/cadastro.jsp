<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TaskManager — Criar Conta</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="auth-body">

<div class="auth-wrapper">

    <div class="auth-logo">
        <div class="mark">T</div>
        <h1>TaskManager</h1>
        <p>Crie sua conta e comece agora</p>
    </div>

    <div class="auth-card">
        <h2 class="auth-card-title">Criar conta</h2>

        <% if (request.getAttribute("erro") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("erro") %></div>
        <% } %>

        <form action="cadastro" method="post" onsubmit="return validarCadastro()">

            <div class="form-group">
                <label for="nome">Nome</label>
                <input type="text" name="nome" id="nome"
                       placeholder="Seu nome completo" autocomplete="name" required>
            </div>

            <div class="form-group">
                <label for="email">E-mail</label>
                <input type="email" name="email" id="email"
                       placeholder="seu@email.com" autocomplete="email" required>
            </div>

            <div class="form-group">
                <label for="senha">Senha</label>
                <div class="input-wrap">
                    <input type="password" name="senha" id="senha"
                           placeholder="Mínimo 6 caracteres" autocomplete="new-password" required>
                    <button type="button" class="btn-eye" onclick="toggleSenha('senha', this)">👁</button>
                </div>
            </div>

            <button type="submit" class="btn btn-primary">Criar conta</button>
        </form>

        <div class="auth-card-footer">
            Já tem conta? <a href="login">Fazer login</a>
        </div>
    </div>

</div>

<script src="js/script.js"></script>
</body>
</html>
