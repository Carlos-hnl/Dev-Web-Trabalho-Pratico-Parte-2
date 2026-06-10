/**
 * Componente reutilizável de botão com variantes e estado de loading.
 */
export default function Button({
  children,
  onClick,
  type = 'button',
  variant = 'primary',
  size = '',
  fullWidth = false,
  loading = false,
  disabled = false,
  className = '',
}) {
  const classes = [
    'btn',
    `btn-${variant}`,
    size ? `btn-${size}` : '',
    fullWidth ? 'btn-full' : '',
    className,
  ]
    .filter(Boolean)
    .join(' ')

  return (
    <button
      type={type}
      className={classes}
      onClick={onClick}
      disabled={disabled || loading}
    >
      {loading ? (
        <>
          <span
            style={{
              width: '1rem',
              height: '1rem',
              border: '2px solid rgba(255,255,255,0.4)',
              borderTopColor: '#fff',
              borderRadius: '50%',
              display: 'inline-block',
              animation: 'spin 0.7s linear infinite',
            }}
          />
          Aguarde...
        </>
      ) : (
        children
      )}
    </button>
  )
}
