import { useState, useEffect, useCallback } from 'react'
import { tarefaService } from '../services/api'


export function useTarefas() {
  const [tarefas, setTarefas] = useState([])
  const [loading, setLoading] = useState(true)
  const [erro, setErro] = useState(null)

  const carregarTarefas = useCallback(async () => {
    setLoading(true)
    setErro(null)
    try {
      const res = await tarefaService.listar()
      if (res.sucesso) {
        setTarefas(res.dados)
      } else {
        setErro(res.erro || 'Erro ao carregar tarefas.')
      }
    } catch {
      setErro('Falha na comunicação com o servidor.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    carregarTarefas()
  }, [carregarTarefas])

  const criar = useCallback(async (titulo, descricao) => {
    const res = await tarefaService.criar(titulo, descricao)
    if (res.sucesso) await carregarTarefas()
    return res
  }, [carregarTarefas])

  const atualizar = useCallback(async (id, titulo, descricao) => {
    const res = await tarefaService.atualizar(id, titulo, descricao)
    if (res.sucesso) await carregarTarefas()
    return res
  }, [carregarTarefas])

  const excluir = useCallback(async (id) => {
    const res = await tarefaService.excluir(id)
    if (res.sucesso) {
      // Atualiza o estado localmente para resposta imediata (sem fetch extra)
      setTarefas((prev) => prev.filter((t) => t.id !== id))
    }
    return res
  }, [])

  const alternarConclusao = useCallback(async (id) => {
    setTarefas((prev) =>
      prev.map((t) => (t.id === id ? { ...t, concluida: !t.concluida } : t))
    )
    const res = await tarefaService.alternarConclusao(id)
    if (!res.sucesso) {
      // Reverte em caso de erro
      setTarefas((prev) =>
        prev.map((t) => (t.id === id ? { ...t, concluida: !t.concluida } : t))
      )
    }
    return res
  }, [])

  // Estatísticas derivadas do estado (sem estado extra desnecessário)
  const stats = {
    total: tarefas.length,
    concluidas: tarefas.filter((t) => t.concluida).length,
    pendentes: tarefas.filter((t) => !t.concluida).length,
  }

  return {
    tarefas,
    loading,
    erro,
    stats,
    criar,
    atualizar,
    excluir,
    alternarConclusao,
    recarregar: carregarTarefas,
  }
}
