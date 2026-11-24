

import {useEffect} from 'react';

import {useAuth} from './contexts/AuthContext.jsx';
import './css/cssHomePage.css';

 function HomePage() {
    
    const {user} = useAuth();


    useEffect(() => {
      console.log("uzytkownik zalogowany:", user);
    }, [user]);

    return (
        <div className="home-page">
            <header className="header">
                <div className="header-logo">
                    OMP 
                </div>
                <nav className="header-buttons" aria-label="Nawigacja główna">
                    <a href="/" className="header-button">Strona główna</a>
                    <a href="/test" className="header-button">Generuj Test</a>

                </nav>
                <div className="user-info">
                    {user ? (
                        <span className="user-name">Witaj, {user.user_metadata.full_name}!</span>
                    ):(
                        <a href="/login" className="header-button">Zaloguj się</a>
                    )
                    }
                </div>
            </header>
            <main className="home-body" aria-label="Treść strony głównej">
                <section className="home-main-block" aria-label="Główny blok treści" />
                <aside className="home-side-block" aria-label="Boczny blok treści" />
                <section className="home-lower-blocks" aria-label="Dodatkowe bloki treści">
                    <div className="home-lower-block" />
                    <div className="home-lower-block" />
                    <div className="home-lower-block" />
                </section>
            </main>
        </div>
    );
}

export default HomePage;