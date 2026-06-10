/**
 * Componente reutilizável de campo de formulário.
 * Usado em LoginPage, CadastroPage e modais de tarefa.
 */
export default function Input({
  label,
  id,
  type = 'text',
  value,
  onChange,
  placeholder = '',
  required = false,
  autoComplete,
  as = 'input',
  rows = 3,
}) {
  const Tag = as

  return (
    <div className="form-group">
      {label && (
        <label className="form-label" htmlFor={id}>
          {label}
          {required && <span style={{ color: 'var(--color-danger)', marginLeft: 2 }}>*</span>}
        </label>
      )}
      <Tag
        id={id}
        type={as === 'input' ? type : undefined}
        className="form-input"
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        autoComplete={autoComplete}
        rows={as === 'textarea' ? rows : undefined}
      />
    </div>
  )
}
