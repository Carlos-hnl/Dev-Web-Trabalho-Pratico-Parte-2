import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Input   from '../components/ui/Input'
import Button  from '../components/ui/Button'
import Alert   from '../components/ui/Alert'

/**
 * Página de Cadastro de novo usuário.
 */
export default function CadastroPage() {
  const { cadastro } = useAuth()
  const navigate     = useNavigate()

  const [nome,    setNome]    = useState('')
  const [email,   setEmail]   = useState('')
  const [senha,   setSenha]   = useState('')
  const [confirma,setConfirma]= useState('')
  const [erro,    setErro]    = useState('')
  const [sucesso, setSucesso] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErro('')
    setSucesso('')

    if (!nome.trim() || !email.trim() || !senha || !confirma) {
      setErro('Preencha todos os campos.')
      return
    }
    if (nome.trim().length < 3) {
      setErro('Nome deve ter ao menos 3 caracteres.')
      return
    }
    if (senha.length < 6) {
      setErro('Senha deve ter ao menos 6 caracteres.')
      return
    }
    if (senha !== confirma) {
      setErro('As senhas não coincidem.')
      return
    }

    setLoading(true)
    const res = await cadastro(nome.trim(), email.trim(), senha)
    setLoading(false)

    if (res.sucesso) {
      setSucesso('Cadastro realizado! Redirecionando para o login...')
      setTimeout(() => navigate('/login', { replace: true }), 1800)
    } else {
      setErro(res.erro || 'Erro ao cadastrar. Tente novamente.')
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">
          <h1>✓ TaskManager</h1>
          <p>Crie sua conta gratuitamente</p>
        </div>

        <Alert message={erro}    type="error"   />
        <Alert message={sucesso} type="success" />

        <form onSubmit={handleSubmit} noValidate>
          <Input
            id="nome"
            label="Nome completo"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            placeholder="João Silva"
            required
            autoComplete="name"
          />
          <Input
            id="email"
            label="E-mail"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="seu@email.com"
            required
            autoComplete="email"
          />
          <Input
            id="senha"
            label="Senha"
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            placeholder="Mínimo 6 caracteres"
            required
            autoComplete="new-password"
          />
          <Input
            id="confirma"
            label="Confirmar senha"
            type="password"
            value={confirma}
            onChange={(e) => setConfirma(e.target.value)}
            placeholder="Repita a senha"
            required
            autoComplete="new-password"
          />

          <Button type="submit" loading={loading} fullWidth>
            Criar conta
          </Button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.875rem', color: 'var(--color-muted)' }}>
          Já tem conta?{' '}
          <Link to="/login">Fazer login</Link>
        </p>
      </div>
    </main>
  )
}
