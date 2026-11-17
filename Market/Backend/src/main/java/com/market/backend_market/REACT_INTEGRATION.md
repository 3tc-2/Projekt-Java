# Market — integracja uwierzytelniania w React

## Zasady

1. Wykonuj żądania z `credentials: "include"`, żeby przeglądarka wysyłała ciasteczko sesyjne.
2. Przed `POST`/`PUT`/`PATCH`/`DELETE` pobierz token z `GET /api/auth/csrf` i przekaż go w nagłówku odpowiednio do pola `headerName`.
3. Po logowaniu i wylogowaniu pobierz nowy token CSRF, jeśli zamierzasz wysłać następne żądanie modyfikujące stan.
4. Nie zapisuj `JSESSIONID`, JWT ani hasła w `localStorage`.
5. Po odświeżeniu strony sprawdź stan sesji przez `GET /api/auth/me`.
6. Backend obecnie dopuszcza originy `http://localhost:3000` i `http://localhost:5173`. Dla innych originów backendowiec musi zaktualizować CORS.

## Prosty klient API (`src/api/auth.js`)

Kod można skopiować do projektu React. Obsługa CSRF jest celowo prosta: pobieramy go przed każdą operacją zmieniającą stan, aby uniknąć użycia nieaktualnego tokenu.

```javascript
const API_URL = "http://localhost:8080";

async function readJson(response) {
  const contentType = response.headers.get("content-type") || "";
  if (!contentType.includes("application/json")) {
    throw new Error(`Nieoczekiwana odpowiedź serwera (${response.status})`);
  }
  return response.json();
}

async function getCsrf() {
  const response = await fetch(`${API_URL}/api/auth/csrf`, {
    credentials: "include",
  });
  const data = await readJson(response);
  if (!response.ok) throw new Error("Nie udało się pobrać tokenu CSRF");
  return data;
}

async function postWithCsrf(path, payload) {
  const csrf = await getCsrf();
  const response = await fetch(`${API_URL}${path}`, {
    method: "POST",
    credentials: "include",
    headers: {
      ...(payload !== undefined ? { "Content-Type": "application/json" } : {}),
      [csrf.headerName]: csrf.token,
    },
    ...(payload !== undefined ? { body: JSON.stringify(payload) } : {}),
  });
  const data = await readJson(response);
  if (!response.ok) throw new Error(data.error || `Błąd HTTP ${response.status}`);
  return data;
}

export function register({ imie, nazwisko, login, haslo, email }) {
  return postWithCsrf("/api/auth/register", {
    imie, nazwisko, login, haslo, email,
  });
}

export function login(login, haslo) {
  return postWithCsrf("/api/auth/login", { login, haslo });
}

export async function getProfile() {
  const response = await fetch(`${API_URL}/api/auth/me`, {
    credentials: "include",
  });
  if (response.status === 401) return null;
  const data = await readJson(response);
  if (!response.ok) throw new Error(data.error || "Nie udało się pobrać profilu");
  return data;
}

export function logout() {
  return postWithCsrf("/api/auth/logout");
}
```

## Przykłady użycia w komponentach

```javascript
import { register, login, getProfile, logout } from "./api/auth";

// Formularz rejestracji:
await register({
  imie: "Jan",
  nazwisko: "Kowalski",
  login: "jan123",
  haslo: "Test12345",
  email: "jan@example.com",
});

// Logowanie:
const result = await login("jan123", "Test12345");
console.log(result.login);

// Sprawdzenie sesji po odświeżeniu strony:
const profile = await getProfile();
if (profile === null) {
  // Użytkownik nie jest zalogowany
}

// Wylogowanie:
await logout();
```

## Stan UI

- Przechowuj profil użytkownika w stanie React (np. Context), a nie w ciasteczku czy `localStorage`.
- Jeśli `GET /api/auth/me` zwróci `null`, pokaż niezalogowanego użytkownika.
- Nie zakładaj, że zwrócone podczas logowania `userId` daje prawo do operacji na wskazanym użytkowniku. Backend musi weryfikować właściciela na podstawie sesji.
- W trakcie działania aplikacji pokaż stan ładowania zanim rozstrzygnie się żądanie `me`, żeby interfejs nie migał pomiędzy stanami.

## Lokalny development

- Frontend React: `http://localhost:5173` (Vite) lub `http://localhost:3000`.
- Backend Spring Boot: `http://localhost:8080`.
- Przeglądarka i backend powinny używać konsekwentnie `localhost` (nie mieszać z `127.0.0.1`), bo ciasteczka i polityka same-site zależą od hosta.
- Na produkcji skonfiguruj HTTPS, bezpieczne atrybuty cookies i rzeczywiste dozwolone originy. Nie kopiuj lokalnej listy CORS bez przeglądu.

## Uwaga o błędach

Gdy `POST` zwraca `403`, najpierw sprawdź, czy `GET /csrf` i POST korzystają z tej samej sesji oraz czy token CSRF jest aktualny. Gdy `GET /me` zwraca `401`, sprawdź `credentials: "include"`, status logowania i politykę cookies. `ECONNREFUSED` oznacza zwykle, że backend nie nasłuchuje na podanym porcie.
