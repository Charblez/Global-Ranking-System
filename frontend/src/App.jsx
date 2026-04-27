import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import HomePage from './pages/HomePage';
import CategoryPage from './pages/CategoryPage';
import CreateCategoryPage from './pages/CreateCategoryPage';
import AuthPage from './pages/AuthPage';
import ProfilePage from './pages/ProfilePage';

function Header({ currentUser, onLogout, darkMode, onToggleDark }) {
  const location = useLocation();

  const isActive = (path) => {
    if (path === '/' && location.pathname === '/') return true;
    if (path !== '/' && location.pathname.startsWith(path)) return true;
    return false;
  };

  return (
    <header className="header">
      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        <Link to="/" className="header-logo">
          <img src="/logo.webp" alt="Logo" />
          <span>StatIt</span>
        </Link>
        
        {/* LIGHT/DARK TOGGLE MOVED TO HEADER */}
        <label className="dark-toggle">
          <span>{darkMode ? 'Dark' : 'Light'}</span>
          <div className={`dark-toggle-track ${darkMode ? 'on' : ''}`} onClick={onToggleDark}>
            <div className="dark-toggle-thumb" />
          </div>
        </label>
      </div>

      <nav className="header-nav">
        <Link to="/" className={`nav-link ${isActive('/') && !isActive('/category') && !isActive('/create') && !isActive('/profile') ? 'active' : ''}`}>
          Home
        </Link>

        {currentUser && (
          <Link to="/profile" className={`nav-link ${isActive('/profile') ? 'active' : ''}`}>
            Profile
          </Link>
        )}

        {currentUser ? (
          <button className="nav-link" type="button" onClick={onLogout}>
            Log out
          </button>
        ) : (
          <Link to="/login" className={`nav-link ${isActive('/login') || isActive('/signup') ? 'active' : ''}`}>
            Login
          </Link>
        )}
      </nav>
    </header>
  );
}

function Footer() {
  return (
    <footer className="footer">
      <div className="footer-left">
        <span>Statit</span>
      </div>
    </footer>
  );
}

function AppContent() {
  const navigate = useNavigate();

  const [currentUser, setCurrentUser] = useState(() => {
    const saved = localStorage.getItem('currentUser');
    return saved ? JSON.parse(saved) : null;
  });

  const [darkMode, setDarkMode] = useState(() => {
    const saved = localStorage.getItem('darkMode');
    return saved === 'true';
  });

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', darkMode ? 'dark' : 'light');
    localStorage.setItem('darkMode', darkMode);
  }, [darkMode]);

  const handleLogin = (userData) => {
    setCurrentUser(userData);
  };

  const handleLogout = () => {
    setCurrentUser(null);
    localStorage.removeItem('currentUser');
    navigate('/login');
  };

  const toggleDark = () => setDarkMode((prev) => !prev);

  return (
    <>
      <Header currentUser={currentUser} onLogout={handleLogout} />

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/category/:categoryId" element={<CategoryPage currentUser={currentUser} />} />
        <Route path="/create" element={currentUser ? <CreateCategoryPage currentUser={currentUser} /> : <Navigate to="/login" />} />
        <Route path="/profile" element={currentUser ? <ProfilePage currentUser={currentUser} /> : <Navigate to="/login" />} />
        <Route path="/login" element={currentUser ? <Navigate to="/" /> : <AuthPage mode="login" onLogin={handleLogin} />} />
        <Route path="/signup" element={currentUser ? <Navigate to="/" /> : <AuthPage mode="signup" onLogin={handleLogin} />} />
      </Routes>

      <Footer darkMode={darkMode} onToggleDark={toggleDark} />
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}