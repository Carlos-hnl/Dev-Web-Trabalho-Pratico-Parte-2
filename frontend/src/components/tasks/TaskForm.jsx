import { useState } from 'react'
import Input from '../ui/Input'
import Button from '../ui/Button'
import Alert from '../ui/Alert'

/**
 * Formulário de criação de nova tarefa.
 * Gerencia seu próprio estado local e chama onCreate ao submeter.
 */
export default function TaskForm({ onCreate }) {
  const [titulo, setTitulo]       = useState('')
  const [descricao, setDescricao] = useState('')
  const [loading, setLoading]     = useState(false)
  const [erro, setErro]           = useState('')
  const [sucesso, setSucesso]     = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErro('')
    setSucesso('')

    if (!titulo.trim()) {
      setErro('O título é obrigatório.')
      return
    }

    setLoading(true)
    const res = await onCreate(titulo.trim(), descricao.trim())
    setLoading(false)

    if (res.sucesso) {
      setSucesso('Tarefa criada com sucesso!')
      setTitulo('')
      setDescricao('')
      // Limpa mensagem de sucesso após 3s
      setTimeout(() => setSucesso(''), 3000)
    } else {
      setErro(res.erro || 'Erro ao criar tarefa.')
    }
  }

  return (
    <div className="card">
      <h2 className="card-title">Nova Tarefa</h2>

      <Alert message={erro}    type="error"   />
      <Alert message={sucesso} type="success" />

      <form onSubmit={handleSubmit} noValidate>
        <Input
          id="titulo"
          label="Título"
          value={titulo}
          onChange={(e) => setTitulo(e.target.value)}
          placeholder="Ex: Estudar React"
          required
        />
        <Input
          id="descricao"
          label="Descrição (opcional)"
          value={descricao}
          onChange={(e) => setDescricao(e.target.value)}
          placeholder="Detalhes sobre a tarefa..."
          as="textarea"
        />
        <Button type="submit" loading={loading} fullWidth>
          + Adicionar Tarefa
        </Button>
      </form>
    </div>
  )
}
