/**
 * Componente de item individual de tarefa.
 * Exibe título, descrição, status e botões de ação.
 */
export default function TaskItem({ tarefa, onToggle, onEdit, onDelete }) {
  const handleToggle = () => onToggle(tarefa.id)
  const handleEdit   = () => onEdit(tarefa)
  const handleDelete = () => {
    if (window.confirm(`Excluir a tarefa "${tarefa.titulo}"?`)) {
      onDelete(tarefa.id)
    }
  }

  return (
    <article
      className={`task-item ${tarefa.concluida ? 'concluida' : ''}`}
      aria-label={`Tarefa: ${tarefa.titulo}`}
    >
      {/* Checkbox de conclusão */}
      <button
        className={`task-checkbox ${tarefa.concluida ? 'checked' : ''}`}
        onClick={handleToggle}
        aria-label={tarefa.concluida ? 'Marcar como pendente' : 'Marcar como concluída'}
        title={tarefa.concluida ? 'Marcar como pendente' : 'Marcar como concluída'}
      >
        {tarefa.concluida && '✓'}
      </button>

      {/* Conteúdo */}
      <div className="task-body">
        <p className="task-title">{tarefa.titulo}</p>
        {tarefa.descricao && (
          <p className="task-desc">{tarefa.descricao}</p>
        )}
      </div>

      {/* Badge de status */}
      <span className={`badge ${tarefa.concluida ? 'badge-done' : 'badge-pending'}`}>
        {tarefa.concluida ? 'Concluída' : 'Pendente'}
      </span>

      {/* Ações */}
      <div className="task-actions">
        <button
          className="btn-icon"
          onClick={handleEdit}
          title="Editar tarefa"
          aria-label="Editar tarefa"
        >
          ✏️
        </button>
        <button
          className="btn-icon danger"
          onClick={handleDelete}
          title="Excluir tarefa"
          aria-label="Excluir tarefa"
        >
          🗑️
        </button>
      </div>
    </article>
  )
}
