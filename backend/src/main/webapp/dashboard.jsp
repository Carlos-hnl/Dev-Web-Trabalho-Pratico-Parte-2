<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <title>TaskManager — Tarefas</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<!-- ── Navbar ─────────────────────────────────────────────── -->
<nav class="navbar">
    <div class="nav-brand">
        <div class="mark">T</div>
        <span>TaskManager</span>
    </div>
    <div class="nav-right">
        <span class="nav-user">Olá, <strong>${usuarioLogado.nome}</strong></span>
        <form action="logout" method="post">
            <button type="submit" class="btn btn-ghost">Sair</button>
        </form>
    </div>
</nav>

<!-- ── Conteúdo ───────────────────────────────────────────── -->
<div class="dashboard-layout">

    <div class="dashboard-header">
        <h2>Minhas Tarefas</h2>
        <p>${tarefasPendentes} pendente(s) · ${tarefasConcluidas} concluída(s)</p>
    </div>

    <!-- Estatísticas -->
    <div class="stats-bar">
        <div class="stat-item">
            <span class="stat-num">${totalTarefas}</span>
            <span class="stat-label">Total</span>
        </div>
        <div class="stat-item pending">
            <span class="stat-num">${tarefasPendentes}</span>
            <span class="stat-label">Pendentes</span>
        </div>
        <div class="stat-item done">
            <span class="stat-num">${tarefasConcluidas}</span>
            <span class="stat-label">Concluídas</span>
        </div>
    </div>

    <!-- Alertas -->
    <c:if test="${not empty param.msg}">
        <div class="alert alert-success">${param.msg}</div>
    </c:if>
    <c:if test="${not empty param.erro}">
        <div class="alert alert-error">${param.erro}</div>
    </c:if>

    <!-- Formulário de nova tarefa -->
    <div class="section-divider"><h3>Nova tarefa</h3></div>

    <form action="tarefa" method="post" onsubmit="return validarTarefa()" class="task-add-form">
        <input type="hidden" name="acao" value="criar">
        <input type="text"   name="titulo"    id="titulo"
               placeholder="O que precisa ser feito?" maxlength="200" required>
        <input type="text"   name="descricao" id="descricao" class="input-desc"
               placeholder="Descrição (opcional)" maxlength="500">
        <button type="submit" class="btn btn-add">+ Adicionar</button>
    </form>

    <!-- Lista de tarefas -->
    <div class="section-divider"><h3>Lista</h3></div>

    <c:choose>
        <c:when test="${empty tarefas}">
            <div class="empty-state">
                <span class="empty-icon">◻</span>
                <p>Nenhuma tarefa ainda</p>
                <small>Adicione sua primeira tarefa acima</small>
            </div>
        </c:when>
        <c:otherwise>
            <ul class="task-list">
                <c:forEach var="tarefa" items="${tarefas}">
                    <li class="task-item ${tarefa.concluida ? 'done' : ''}">

                        <!-- Botão de status circular -->
                        <form action="tarefa" method="post" style="display:contents">
                            <input type="hidden" name="acao" value="concluir">
                            <input type="hidden" name="id"   value="${tarefa.id}">
                            <button type="submit" class="btn-status" title="${tarefa.concluida ? 'Reabrir' : 'Marcar como concluída'}">✓</button>
                        </form>

                        <!-- Título e descrição  -->
                        <div class="task-body"
                             onclick="abrirEdicao(${tarefa.id}, '${fn:escapeXml(tarefa.titulo)}', '${fn:escapeXml(tarefa.descricao)}')"
                             title="Clique para editar">
                            <div class="task-title">${fn:escapeXml(tarefa.titulo)}</div>
                            <c:if test="${not empty tarefa.descricao}">
                                <div class="task-desc-text">${fn:escapeXml(tarefa.descricao)}</div>
                            </c:if>
                        </div>

                        <!-- Ações (aparecem no hover) -->
                        <div class="task-actions">
                            <button type="button" class="btn btn-action"
                                    onclick="abrirEdicao(${tarefa.id}, '${fn:escapeXml(tarefa.titulo)}', '${fn:escapeXml(tarefa.descricao)}')">
                                Editar
                            </button>
                            <form action="tarefa" method="post" style="display:contents"
                                  onsubmit="return confirm('Deseja excluir esta tarefa?')">
                                <input type="hidden" name="acao" value="excluir">
                                <input type="hidden" name="id"   value="${tarefa.id}">
                                <button type="submit" class="btn btn-danger">Excluir</button>
                            </form>
                        </div>

                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>

</div>


<div id="modalEdicao" class="modal-overlay">
    <div class="modal">
        <div class="modal-header">
            <h3>Editar Tarefa</h3>
            <button class="modal-close" onclick="fecharModal()">✕</button>
        </div>
        <form action="tarefa" method="post">
            <input type="hidden" name="acao"   value="editar">
            <input type="hidden" name="id"     id="editId">
            <div class="form-group">
                <label for="editTitulo">Título</label>
                <input type="text" name="titulo" id="editTitulo" maxlength="200" required>
            </div>
            <div class="form-group">
                <label for="editDescricao">Descrição</label>
                <textarea name="descricao" id="editDescricao" maxlength="500"></textarea>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn btn-ghost" onclick="fecharModal()">Cancelar</button>
                <button type="submit" class="btn btn-add">Salvar</button>
            </div>
        </form>
    </div>
</div>

<script src="js/script.js"></script>
</body>
</html>
