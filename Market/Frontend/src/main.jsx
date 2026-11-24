import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { AuthProvider } from './contexts/AuthContext.jsx';
import  HomePage  from './HomePage.jsx';
import Auth from './Auth.jsx';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
          {/* <Route path="/quiz" element={<QuizPage />} /> */}
          <Route path="/login" element={<Auth />} />
          <Route path="*" element={<h2>404 - Nie ma takiej strony</h2>} />
        </Routes>
      </Router>
    </AuthProvider>
  </StrictMode>,
)
