import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { authService } from '../services/api'


const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [loading, setLoading] = useState(true) // verifica sessão no mount

  // Verifica sessão existente ao carregar o app
  useEffect(() => {
    authService
      .me()
      .then((res) => {
        if (res.sucesso) setUsuario(res.dados)
      })
      .catch(() => {}) // silencia erro 401 (não logado)
      .finally(() => setLoading(false))
  }, [])

  const login = useCallback(async (email, senha) => {
    const res = await authService.login(email, senha)
    if (res.sucesso) {
      setUsuario(res.dados)
    }
    return res
  }, [])

  const logout = useCallback(async () => {
    await authService.logout()
    setUsuario(null)
  }, [])

  const cadastro = useCallback(async (nome, email, senha) => {
    return authService.cadastro(nome, email, senha)
  }, [])

  return (
    <AuthContext.Provider value={{ usuario, loading, login, logout, cadastro }}>
      {children}
    </AuthContext.Provider>
  )
}

/** Hook para consumir o contexto de autenticação. */
export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth deve ser usado dentro de <AuthProvider>')
  return ctx
}
