import { useState } from 'react'
import TaskItem from './TaskItem'

const FILTROS = [
  { key: 'todas',     label: 'Todas'     },
  { key: 'pendentes', label: 'Pendentes' },
  { key: 'concluidas',label: 'Concluídas'},
]

/**
 * Lista de tarefas com filtros por status.
 * Renderiza condicionalmente apenas os itens do filtro ativo.
 */
export default function TaskList({ tarefas, onToggle, onEdit, onDelete }) {
  const [filtroAtivo, setFiltroAtivo] = useState('todas')

  const tarefasFiltradas = tarefas.filter((t) => {
    if (filtroAtivo === 'pendentes')  return !t.concluida
    if (filtroAtivo === 'concluidas') return t.concluida
    return true
  })

  return (
    <div className="card">
      <h2 className="card-title">Minhas Tarefas</h2>

      {/* Filtros */}
      <div className="task-filters" role="tablist" aria-label="Filtrar tarefas">
        {FILTROS.map((f) => (
          <button
            key={f.key}
            className={`filter-btn ${filtroAtivo === f.key ? 'active' : ''}`}
            onClick={() => setFiltroAtivo(f.key)}
            role="tab"
            aria-selected={filtroAtivo === f.key}
          >
            {f.label}
          </button>
        ))}
      </div>

      {/* Lista ou estado vazio */}
      {tarefasFiltradas.length === 0 ? (
        <div className="task-empty" role="status">
          <div className="empty-icon">
            {filtroAtivo === 'concluidas' ? '🏆' : '📋'}
          </div>
          <p>
            {filtroAtivo === 'todas'
              ? 'Nenhuma tarefa ainda. Crie sua primeira!'
              : filtroAtivo === 'pendentes'
              ? 'Nenhuma tarefa pendente. Tudo em dia!'
              : 'Nenhuma tarefa concluída ainda.'}
          </p>
        </div>
      ) : (
        <div className="task-list" role="list">
          {tarefasFiltradas.map((tarefa) => (
            <TaskItem
              key={tarefa.id}
              tarefa={tarefa}
              onToggle={onToggle}
              onEdit={onEdit}
              onDelete={onDelete}
            />
          ))}
        </div>
      )}
    </div>
  )
}
