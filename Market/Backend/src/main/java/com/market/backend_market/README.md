# Market Backend — dokumentacja integracji frontendu

**Zakres:** rejestracja, logowanie, sesja HTTP, profil użytkownika i wylogowanie.

Dokumentacja dotyczy części uwierzytelniania backendu Market (Spring Boot + Spring Security + JDBC + MySQL/MariaDB). Nie opisuje API ogłoszeń, portfeli ani zamówień — te moduły rozwija druga osoba.

## Dokumenty

| Plik | Zawartość |
| --- | --- |
| [API_AUTH.md](API_AUTH.md) | Endpointy, pola JSON, statusy i przykłady odpowiedzi |
| [REACT_INTEGRATION.md](REACT_INTEGRATION.md) | Integracja `fetch`, ciasteczka sesji, CSRF i przykładowy klient API |
| [TESTOWANIE.md](TESTOWANIE.md) | Konfiguracja Postmana i scenariusze testowe |

## Ustalenia techniczne

- Lokalny adres backendu: `http://localhost:8080` (o ile nie zmieniono `server.port`).
- Rejestracja i logowanie: **login + hasło**, nie adres e-mail.
- Sesja HTTP przechowywana po stronie backendu; przeglądarka otrzymuje ciasteczko `JSESSIONID`.
- Frontend powinien stosować `credentials: "include"` dla każdego wywołania API.
- Operacje zmieniające stan wymagają tokenu CSRF w nagłówku `X-CSRF-TOKEN`.
- Dostępne lokalne źródła CORS w aktualnej konfiguracji: `http://localhost:3000`, `http://localhost:5173`.
- Żadnego JWT ani własnego tokenu dostępowego w `localStorage`.

## Aktualny stan testów

- Z relacji z dotychczasowych ręcznych testów: **rejestracja, logowanie oraz `GET /api/auth/me` działają**.
- **Wylogowanie i dostęp po wylogowaniu**: endpoint przygotowany, ale test nie został jeszcze potwierdzony.
- Specyfikację opracowano na podstawie przesłanego kodu `src.zip`, przygotowanego wcześniej pakietu uwierzytelniania i późniejszych ustaleń. Ostateczny stan należy zweryfikować z bieżącym repozytorium zespołu.

## Szybka sekwencja dla frontendu

1. `GET /api/auth/csrf` — odbierz token i zachowaj cookie sesyjne.
2. `POST /api/auth/register` — utwórz konto (z tokenem CSRF).
3. `GET /api/auth/csrf` — pobierz aktualny token, gdy potrzeba.
4. `POST /api/auth/login` — zaloguj (z tokenem CSRF).
5. `GET /api/auth/me` — sprawdź zalogowanego użytkownika.
6. `GET /api/auth/csrf` — pobierz świeży token po uwierzytelnieniu.
7. `POST /api/auth/logout` — wyloguj (z tokenem CSRF).

> **Ważne:** obecnie rejestracja nie loguje użytkownika automatycznie. Po rejestracji należy wykonać osobne logowanie.
