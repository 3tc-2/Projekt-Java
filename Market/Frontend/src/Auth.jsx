// src/components/Auth.jsx
import { useState } from 'react';
import { supabase } from './supabaseclient.js';
import { useAuth } from './contexts/AuthContext.jsx';
import './css/cssAuth.css';
import { FaArrowLeft } from "react-icons/fa";

import {motion} from 'framer-motion';

export default function Auth() {
  const [isSignUp, setIsSignUp] = useState(false); // false = Logowanie, true = Rejestracja
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const { user } = useAuth();

  const handleAuth = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage('');

    try {
      if (isSignUp) {
        // REJESTRACJA
        const { error } = await supabase.auth.signUp({
          email,
          password,
          options: {
            // Te dane nasz Trigger w SQL automatycznie wrzuci do tabeli profiles!
            data: { full_name: fullName },
          },
        });
        if (error) throw error;
        alert('Konto założone pomyślnie! Zostałeś automatycznie zalogowany.');
      } else {
        // LOGOWANIE
        const { error } = await supabase.auth.signInWithPassword({
          email,
          password,
        });
        if (error){ throw error}else{
          window.location.href = '/';
        };
      }
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setLoading(false);    
    }
  };

  return (
    <div className="auth-container">
      <motion.div 
        className="auth-back-button" 
        onClick={() => window.history.back()} 
        initial={{ scale: 1, rotate: 0 ,x:0}}
        whileHover={{  scale: 1.2,rotate:[0,20,0],transition:{duration:0.1} }}
        whileTap={{ scale: 0.9,x:[0,-10],transition:{duration:0.2} }}
      >
        <FaArrowLeft></FaArrowLeft>
      </motion.div>
      <h2 className="auth-title">
        {user === null ? 'Stwórz konto 🚀' : 'Witaj ponownie 👋'}
      </h2>

      {errorMessage && (
        <div className="auth-error">
          {errorMessage}
        </div>
      )}

      <form onSubmit={handleAuth} className="auth-form">
        {isSignUp && (
          <div className="auth-field">
            <label className="auth-label">Imię i nazwisko / Nick</label>
            <input
              type="text"
              required
              placeholder="np. Jan Kowalski"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              className="auth-input"
            />
          </div>
        )}

        <div className="auth-field">
          <label className="auth-label">Adres E-mail</label>
          <input
            type="email"
            required
            placeholder="twoj@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="auth-input"
          />
        </div>

        <div className="auth-field">
          <label className="auth-label">Hasło</label>
          <input
            type="password"
            required
            placeholder="••••••••"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="auth-input"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="auth-submit"
          onClick={handleAuth}
        >
          {loading ? 'Przetwarzanie...' : (isSignUp ? 'Zarejestruj się' : 'Zaloguj się')}
        </button>
      </form>

      <div className="auth-toggle">
        {isSignUp ? 'Masz już konto?' : 'Nie masz jeszcze konta?'}{' '}
        <button
          type="button"
          onClick={() => { setIsSignUp(!isSignUp); setErrorMessage(''); }}
          className="auth-toggle-button"
        >
          {isSignUp ? 'Zaloguj się' : 'Zarejestruj się'}
        </button>
      </div>
    </div>
  );
}