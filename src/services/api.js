/**
 * Camada de Serviço — centraliza todas as chamadas à API Java.
 * Usa Fetch API com async/await e tratamento de erros padronizado.
 *
 * A baseURL deve apontar para onde o Tomcat está rodando.
 * Em desenvolvimento: http://localhost:8080/api
 * Em produção: coloque a URL do servidor remoto em .env
 */
const BASE_URL = import.meta.env.VITE_API_URL || 'https://carlos-hnl.github.io/Dev-Web-Trabalho-Pratico-Parte-2'

/**
 * Faz uma requisição à API com tratamento de erro centralizado.
 * @param {string} endpoint - caminho relativo, ex: '/auth/login'
 * @param {RequestInit} options - opções do fetch (method, body, etc.)
 * @returns {Promise<{sucesso: boolean, dados?: any, erro?: string, mensagem?: string}>}
 */
async function request(endpoint, options = {}) {
  const url = `${BASE_URL}${endpoint}`

  const config = {
    credentials: 'include',          // envia cookies de sessão
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  }

  try {
    const response = await fetch(url, config)
    const data = await response.json()

    // A API retorna sempre { sucesso: bool, dados/mensagem/erro }
    return { httpStatus: response.status, ...data }
  } catch (error) {
    // Erro de rede ou servidor fora do ar
    console.error(`[API] Erro em ${endpoint}:`, error)
    return {
      sucesso: false,
      erro: 'Não foi possível conectar ao servidor. Verifique se a API está rodando.',
    }
  }
}

// ── Auth ─────────────────────────────────────────────────────

export const authService = {
  /** Verifica se o usuário está logado (checa a sessão). */
  me: () => request('/auth/me'),

  /** Autentica o usuário. */
  login: (email, senha) =>
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha }),
    }),

  /** Cadastra novo usuário. */
  cadastro: (nome, email, senha) =>
    request('/auth/cadastro', {
      method: 'POST',
      body: JSON.stringify({ nome, email, senha }),
    }),

  /** Encerra a sessão. */
  logout: () => request('/auth/logout', { method: 'POST' }),
}

// ── Tarefas ───────────────────────────────────────────────────

export const tarefaService = {
  /** Lista todas as tarefas do usuário logado. */
  listar: () => request('/tarefas'),

  /** Cria uma nova tarefa. */
  criar: (titulo, descricao) =>
    request('/tarefas', {
      method: 'POST',
      body: JSON.stringify({ titulo, descricao }),
    }),

  /** Atualiza título e descrição de uma tarefa. */
  atualizar: (id, titulo, descricao) =>
    request(`/tarefas/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ titulo, descricao }),
    }),

  /** Alterna o status de conclusão da tarefa. */
  alternarConclusao: (id) =>
    request(`/tarefas/${id}/concluir`, { method: 'PATCH' }),

  /** Exclui uma tarefa. */
  excluir: (id) =>
    request(`/tarefas/${id}`, { method: 'DELETE' }),
}
