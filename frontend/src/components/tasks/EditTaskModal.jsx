import { useState, useEffect } from 'react'
import Modal from '../ui/Modal'
import Input from '../ui/Input'
import Button from '../ui/Button'
import Alert from '../ui/Alert'

/**
 * Modal de edição de tarefa existente.
 * Recebe a tarefa a editar e chama onSave ao confirmar.
 */
export default function EditTaskModal({ tarefa, onClose, onSave }) {
  const [titulo, setTitulo]       = useState('')
  const [descricao, setDescricao] = useState('')
  const [loading, setLoading]     = useState(false)
  const [erro, setErro]           = useState('')

  // Popula os campos quando a tarefa muda
  useEffect(() => {
    if (tarefa) {
      setTitulo(tarefa.titulo || '')
      setDescricao(tarefa.descricao || '')
      setErro('')
    }
  }, [tarefa])

  const handleSave = async () => {
    if (!titulo.trim()) {
      setErro('O título é obrigatório.')
      return
    }
    setLoading(true)
    const res = await onSave(tarefa.id, titulo.trim(), descricao.trim())
    setLoading(false)

    if (res.sucesso) {
      onClose()
    } else {
      setErro(res.erro || 'Erro ao atualizar tarefa.')
    }
  }

  return (
    <Modal
      isOpen={!!tarefa}
      onClose={onClose}
      title="Editar Tarefa"
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={loading}>
            Cancelar
          </Button>
          <Button onClick={handleSave} loading={loading}>
            Salvar
          </Button>
        </>
      }
    >
      <Alert message={erro} type="error" />

      <Input
        id="edit-titulo"
        label="Título"
        value={titulo}
        onChange={(e) => setTitulo(e.target.value)}
        placeholder="Título da tarefa"
        required
      />
      <Input
        id="edit-descricao"
        label="Descrição"
        value={descricao}
        onChange={(e) => setDescricao(e.target.value)}
        placeholder="Descrição opcional..."
        as="textarea"
      />
    </Modal>
  )
}
