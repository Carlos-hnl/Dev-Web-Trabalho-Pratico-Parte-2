import { useEffect } from 'react'

/**
 * Componente de modal reutilizável.
 * Fecha ao pressionar Esc ou clicar no overlay.
 */
export default function Modal({ isOpen, onClose, title, children, footer }) {
  // Fecha com Esc
  useEffect(() => {
    if (!isOpen) return
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') onClose()
    }
    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [isOpen, onClose])

  if (!isOpen) return null

  return (
    <div
      className="modal-overlay"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose()
      }}
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
    >
      <div className="modal">
        <div className="modal-header">
          <h2 className="modal-title" id="modal-title">
            {title}
          </h2>
          <button className="modal-close" onClick={onClose} aria-label="Fechar modal">
            ✕
          </button>
        </div>

        {children}

        {footer && <div className="modal-footer">{footer}</div>}
      </div>
    </div>
  )
}
