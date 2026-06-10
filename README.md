# ✓ TaskManager — Parte 2: React + API REST

Refatoração do sistema TaskManager com arquitetura desacoplada:
- Front-end: React (Vite) → hospedado no GitHub Pages
- Back-end: Java Servlets atuando exclusivamente como API REST (JSON)

---

## Observação sobre a arquitetura

O front-end encontra-se publicado no GitHub Pages.

O back-end foi desenvolvido como uma API REST em Java Servlets e deve ser executado localmente conforme as instruções deste README.

A comunicação entre as camadas é realizada através de requisições HTTP assíncronas utilizando JSON.



##  Como rodar o Back-end (API Java) localmente

### Pré-requisitos
- Java 17+
- Maven 3.8+
- PostgreSQL 14+ em execução

### 1. Configurar o banco de dados

```bash
# Criar o banco
psql -U postgres -c "CREATE DATABASE taskmanager;"

# Executar o schema
psql -U postgres -d taskmanager -f backend/schema.sql
```

### 2. Configurar variáveis de ambiente 

| Variável      | Padrão                                        | Descrição            |
|---------------|-----------------------------------------------|----------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:5432/taskmanager`| URL JDBC do banco    |
| `DB_USER`     | `postgres`                                    | Usuário do banco     |
| `DB_PASSWORD` | `postgres`                                    | Senha do banco       |

```bash
# Exemplo Linux/Mac
export DB_URL=jdbc:postgresql://localhost:5432/taskmanager
export DB_USER=meu_usuario
export DB_PASSWORD=minha_senha
```

### 3. Compilar e iniciar o servidor

```bash
cd backend/
mvn clean package
mvn tomcat7:run
```

Após iniciar o servidor, a API estará disponível através dos endpoints:


```text
http://localhost:8080/api/auth/login
http://localhost:8080/api/auth/cadastro
http://localhost:8080/api/tarefas
```

Observação: a rota `/api` não possui uma página inicial e pode retornar HTTP 404. Utilize os endpoints específicos da API.

---

##  Como rodar o Front-end React localmente

### Pré-requisitos
- Node.js 18+
- npm 9+

### 1. Instalar dependências

```bash
cd frontend/
npm install
```

### 2. Configurar a URL da API

```bash
cp .env.example .env
```

Edite o arquivo `.env` caso seja necessário alterar a URL da API.

Exemplo:

```env
VITE_API_URL=http://localhost:8080/api
```

### 3. Iniciar em modo desenvolvimento

```bash
npm run dev
```

O front-end ficará em: **`http://localhost:5173`**

---

##  Deploy — GitHub Pages


O front-end é publicado automaticamente no GitHub Pages através do GitHub Actions.

Qualquer alteração enviada para a branch `main` dispara o workflow de build e publicação.

 Front-end publicado:
https://carlos-hnl.github.io/Dev-Web-Trabalho-Pratico-Parte-2/

---

##  Testando a API com Bruno

1. Instale o [Bruno](https://www.usebruno.com/)
2. Abra o Bruno e clique em Open Collection
3. Selecione a pasta `bruno-collection/taskmanager-api/`
4. Selecione o ambiente local (canto superior direito)
5. Execute os requests na seguinte ordem:

	1. auth-cadastro
	2. auth-login
	3. auth-me
	4. tarefas-post
	5. tarefas-get
	6. tarefas-put
	7. tarefas-patch-concluir
	8. tarefas-delete
	9. auth-logout

> Observação:
> Os testes PUT, PATCH e DELETE utilizam IDs de tarefas previamente cadastradas.
> Caso o banco esteja vazio ou os IDs não existam, execute primeiro o teste de criação (POST) e ajuste o ID da requisição conforme necessário.

### Testes Validados

Foram executados com sucesso os seguintes cenários:

- Cadastro de usuário
- Login
- Recuperação do usuário autenticado
- Logout
- Criação de tarefas
- Listagem de tarefas
- Atualização de tarefas
- Conclusão de tarefas
- Exclusão de tarefas

Todos os endpoints retornam respostas JSON padronizadas utilizando os campos `sucesso`, `dados`, `mensagem` e `erro`.


### Formato de resposta padrão (JSON)

```json
// Sucesso com dados
{ "sucesso": true, "dados": { ... } }

// Sucesso com mensagem
{ "sucesso": true, "mensagem": "Tarefa criada com sucesso!" }

// Erro
{ "sucesso": false, "erro": "Mensagem de erro descritiva" }
```

---

##  Arquitetura e Decisões de Projeto


### Componentes React reutilizáveis

- **`Input`** — campo de formulário com label e validação visual
- **`Button`** — botão com variantes (primary, danger, ghost) e estado de loading
- **`Alert`** — feedback de erro/sucesso/info
- **`Modal`** — dialog acessível (Esc para fechar, click no overlay)
- **`Navbar`** — barra de navegação com dados do usuário
- **`StatsCards`** — cards de estatísticas do dashboard
- **`TaskForm`** — formulário de criação de tarefa
- **`TaskItem`** — item individual com ações
- **`TaskList`** — lista com filtros (Todas / Pendentes / Concluídas)
- **`EditTaskModal`** — modal de edição reutilizando Modal + Input

### Gerenciamento de estado

| Estado        | Onde vive         | Por quê                                      |
|---------------|-------------------|----------------------------------------------|
| Usuário logado| `AuthContext`     | Global — necessário em toda a aplicação      |
| Lista tarefas | `useTarefas` hook | Encapsula fetch + otimistic updates          |
| Formulários   | Estado local      | Escopo restrito ao componente                |
| Modal aberto  | `DashboardPage`   | Coordena TaskList → EditTaskModal            |

---

##  Segurança

- Senhas com hash **BCrypt** (fator 12)
- Autenticação via **Sessão HTTP** (cookie `JSESSIONID`)
- **AuthFilter** protege todas as rotas `/api/tarefas/*`
- **CorsFilter** configura CORS com `credentials: include`
- Queries com **PreparedStatement** (proteção contra SQL Injection)
- Usuário só acessa/modifica suas próprias tarefas (`WHERE usuario_id = ?`)
