import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import LoginPage    from './pages/LoginPage'
import CadastroPage from './pages/CadastroPage'
import DashboardPage from './pages/DashboardPage'
import './styles/global.css'

/**
 * Rota protegida: redireciona para /login se não estiver autenticado.
 */
function RotaProtegida({ children }) {
  const { usuario, loading } = useAuth()

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="spinner" />
        <p>Carregando...</p>
      </div>
    )
  }

  return usuario ? children : <Navigate to="/login" replace />
}

/**
 * Rota pública: redireciona para /dashboard se já estiver autenticado.
 */
function RotaPublica({ children }) {
  const { usuario, loading } = useAuth()

  if (loading) return null

  return usuario ? <Navigate to="/dashboard" replace /> : children
}

export default function App() {
  return (
    <BrowserRouter basename={import.meta.env.BASE_URL}>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />

          <Route
            path="/login"
            element={
              <RotaPublica>
                <LoginPage />
              </RotaPublica>
            }
          />

          <Route
            path="/cadastro"
            element={
              <RotaPublica>
                <CadastroPage />
              </RotaPublica>
            }
          />

          <Route
            path="/dashboard"
            element={
              <RotaProtegida>
                <DashboardPage />
              </RotaProtegida>
            }
          />

          {/* Rota 404 → redireciona para login */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
