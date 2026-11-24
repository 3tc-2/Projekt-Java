// src/context/AuthContext.jsx
import { createContext, useContext, useEffect, useState } from 'react';
import { supabase } from '../supabaseclient.js';

const AuthContext = createContext({});

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  // Funkcja pobierająca profil z tabeli 'profiles' (gdzie jest informacja o Premium!)
  const fetchProfile = async (userId) => {
    try {
      const { data, error } = await supabase
        .from('profiles')
        .select('*')
        .eq('id', userId)
        .single();

      if (!error && data) {
        setProfile(data);
      }
    } catch (err) {
      console.error('Błąd pobierania profilu:', err);
    }
  };

  useEffect(() => {
    // 1. Sprawdź, czy użytkownik jest już zalogowany przy starcie
    supabase.auth.getSession().then(({ data: { session } }) => {
      setUser(session?.user ?? null);
      if (session?.user) {
        fetchProfile(session.user.id);
      }
      setLoading(false);
    });

    // 2. Słuchaj zmian (zalogowanie, wylogowanie) na żywo
    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      setUser(session?.user ?? null);
      if (session?.user) {
        fetchProfile(session.user.id);
      } else {
        setProfile(null);
      }
      setLoading(false);
    });

    return () => subscription.unsubscribe();
  }, []);

  // Funkcja do wylogowywania
  const signOut = () => supabase.auth.signOut();

  return (
    <AuthContext.Provider value={{ user, profile, loading, signOut }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

// Własny hook, żeby łatwiej używać kontekstu w innych plikach
export const useAuth = () => useContext(AuthContext);