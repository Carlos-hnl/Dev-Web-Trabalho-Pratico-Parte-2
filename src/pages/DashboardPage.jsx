import { useState } from 'react'
import Navbar        from '../components/layout/Navbar'
import StatsCards    from '../components/tasks/StatsCards'
import TaskForm      from '../components/tasks/TaskForm'
import TaskList      from '../components/tasks/TaskList'
import EditTaskModal from '../components/tasks/EditTaskModal'
import Alert         from '../components/ui/Alert'
import { useTarefas } from '../hooks/useTarefas'

/**
 * Página principal do dashboard.
 * Compõe todos os componentes de tarefas e gerencia o modal de edição.
 * Toda a lógica de dados vem do hook useTarefas.
 */
export default function DashboardPage() {
  const {
    tarefas,
    loading,
    erro,
    stats,
    criar,
    atualizar,
    excluir,
    alternarConclusao,
  } = useTarefas()

  const [tarefaEditando, setTarefaEditando] = useState(null)

  return (
    <div className="dashboard-layout">
      <Navbar />

      <main className="main-content" id="main-content">
        {/* Cards de estatísticas */}
        <StatsCards stats={stats} />

        {/* Formulário de nova tarefa */}
        <TaskForm onCreate={criar} />

        {/* Erro de carregamento */}
        {erro && <Alert message={erro} type="error" />}

        {/* Lista de tarefas */}
        {loading ? (
          <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
            <div className="spinner" style={{ margin: '0 auto' }} />
            <p style={{ color: 'var(--color-muted)', marginTop: '1rem' }}>
              Carregando tarefas...
            </p>
          </div>
        ) : (
          <TaskList
            tarefas={tarefas}
            onToggle={alternarConclusao}
            onEdit={setTarefaEditando}
            onDelete={excluir}
          />
        )}
      </main>

      {/* Modal de edição (renderiza condicionalmente) */}
      <EditTaskModal
        tarefa={tarefaEditando}
        onClose={() => setTarefaEditando(null)}
        onSave={atualizar}
      />
    </div>
  )
}
