/**
 * Componente de cards de estatísticas do dashboard.
 * Exibe total, pendentes e concluídas de forma visual.
 */
export default function StatsCards({ stats }) {
  const cards = [
    {
      label: 'Total de Tarefas',
      value: stats.total,
      className: 'stat-total',
    },
    {
      label: 'Pendentes',
      value: stats.pendentes,
      className: 'stat-pending',
    },
    {
      label: 'Concluídas',
      value: stats.concluidas,
      className: 'stat-done',
    },
  ]

  return (
    <div className="stats-grid" aria-label="Resumo de tarefas">
      {cards.map((card) => (
        <div key={card.label} className={`stat-card ${card.className}`}>
          <div className="stat-number">{card.value}</div>
          <div className="stat-label">{card.label}</div>
        </div>
      ))}
    </div>
  )
}
