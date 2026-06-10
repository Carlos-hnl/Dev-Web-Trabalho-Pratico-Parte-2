import { useAuth } from '../../context/AuthContext'
import Button from '../ui/Button'

/**
 * Barra de navegação principal do dashboard.
 * Exibe nome do usuário e botão de logout.
 */
export default function Navbar() {
  const { usuario, logout } = useAuth()

  const handleLogout = async () => {
    await logout()
  }

  return (
    <nav className="navbar" role="navigation" aria-label="Navegação principal">
      <span className="navbar-brand">✓ TaskManager</span>

      <div className="navbar-user">
        <span>Olá, {usuario?.nome?.split(' ')[0]}!</span>
        <Button variant="ghost" size="sm" onClick={handleLogout}>
          Sair
        </Button>
      </div>
    </nav>
  )
}
