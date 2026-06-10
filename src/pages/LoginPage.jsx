import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Input   from '../components/ui/Input'
import Button  from '../components/ui/Button'
import Alert   from '../components/ui/Alert'

/**
 * Página de Login.
 * Usa useAuth para autenticar e redireciona para o dashboard.
 */
export default function LoginPage() {
  const { login }   = useAuth()
  const navigate    = useNavigate()

  const [email,  setEmail]  = useState('')
  const [senha,  setSenha]  = useState('')
  const [erro,   setErro]   = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErro('')

    if (!email.trim() || !senha.trim()) {
      setErro('Preencha todos os campos.')
      return
    }

    setLoading(true)
    const res = await login(email.trim(), senha)
    setLoading(false)

    if (res.sucesso) {
      navigate('/dashboard', { replace: true })
    } else {
      setErro(res.erro || 'Credenciais inválidas.')
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">
          <h1>✓ TaskManager</h1>
          <p>Organize suas tarefas com eficiência</p>
        </div>

        <Alert message={erro} type="error" />

        <form onSubmit={handleSubmit} noValidate>
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
            placeholder="Sua senha"
            required
            autoComplete="current-password"
          />

          <Button type="submit" loading={loading} fullWidth>
            Entrar
          </Button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.875rem', color: 'var(--color-muted)' }}>
          Não tem conta?{' '}
          <Link to="/cadastro">Cadastre-se grátis</Link>
        </p>
      </div>
    </main>
  )
}
