# Market — API uwierzytelniania (frontend)

## Informacje podstawowe

- **Base URL (lokalnie):** `http://localhost:8080`
- **Content-Type dla JSON:** `application/json`
- **Uwierzytelnianie:** sesja HTTP przez cookie `JSESSIONID`
- **CSRF:** wymagany dla żądań zmieniających stan, w tym `POST`
- **React / fetch:** `credentials: "include"`
- **Login:** pole `login` (nie `email`)

Odpowiedzi pokazane poniżej są przykładami — ID użytkownika i token CSRF są dynamiczne.

## Endpointy — skrót

| Metoda | Ścieżka | Dostęp | Cel |
| --- | --- | --- | --- |
| GET | `/api/auth/csrf` | Publiczny | Pobranie tokenu CSRF |
| POST | `/api/auth/register` | Publiczny | Rejestracja |
| POST | `/api/auth/login` | Publiczny | Logowanie i utworzenie sesji |
| GET | `/api/auth/me` | Zalogowany | Dane bieżącego użytkownika |
| POST | `/api/auth/logout` | Zalogowany (w praktyce wylogowanie sesji) | Usunięcie sesji |

### `GET /api/auth/csrf`

Bez Body. Zwraca `200 OK`:

```json
{
  "headerName": "X-CSRF-TOKEN",
  "token": "<token-otrzymany-z-serwera>"
}
```

Token dołączaj do kolejnych żądań `POST`, `PUT`, `PATCH` i `DELETE` w nagłówku wskazanym przez `headerName`. Korzystaj z tej samej sesji/cookie. Token może zmienić się po logowaniu lub wylogowaniu — wówczas pobierz go ponownie.

### `POST /api/auth/register`

**Nagłówki:** `Content-Type: application/json`, `X-CSRF-TOKEN: <token>`.

**Body:**

```json
{
  "imie": "Jan",
  "nazwisko": "Kowalski",
  "login": "jan123",
  "haslo": "Test12345",
  "email": "jan@example.com"
}
```

**Sukces — `201 Created`:**

```json
{
  "message": "Użytkownik został zarejestrowany",
  "userId": 1
}
```

**Błąd danych lub zajętego loginu/e-maila — `400 Bad Request`:**

```json
{ "error": "Podany login jest już zajęty" }
```

Inne przykładowe komunikaty z aktualnej usługi: `Podany adres email jest już zajęty`, `Wszystkie pola są wymagane`, `Hasło musi mieć minimum 8 znaków`.

**Uwagi:** pola wymagane: `imie`, `nazwisko`, `login`, `haslo`, `email`. Rejestracja nie ustanawia automatycznie sesji zalogowanego użytkownika. Sposób odpowiedzi na inne błędy (np. format e-maila, konflikty równoczesnej rejestracji) może wymagać dalszego dopracowania backendu.

### `POST /api/auth/login`

**Nagłówki:** `Content-Type: application/json`, `X-CSRF-TOKEN: <token>`.

**Body:**

```json
{
  "login": "jan123",
  "haslo": "Test12345"
}
```

**Sukces — `200 OK`:**

```json
{
  "message": "Zalogowano pomyślnie",
  "userId": 1,
  "login": "jan123"
}
```

Backend utrzymuje logowanie w sesji HTTP (`JSESSIONID`); frontend nie otrzymuje JWT.

**Niepoprawne dane — `401 Unauthorized`:**

```json
{ "error": "Nieprawidłowy login lub hasło" }
```

**Brak loginu lub hasła — `400 Bad Request`:**

```json
{ "error": "Podaj login i hasło" }
```

Po zalogowaniu pobierz świeży token przez `GET /api/auth/csrf` na potrzeby dalszych żądań POST.

### `GET /api/auth/me`

Wymaga ważnej sesji. Bez Body i bez nagłówka CSRF.

**Sukces — `200 OK`:**

```json
{
  "userId": 1,
  "login": "jan123",
  "imie": "Jan",
  "nazwisko": "Kowalski",
  "email": "jan@example.com"
}
```

**Brak ważnej sesji — `401 Unauthorized`:**

```json
{ "error": "Wymagane logowanie" }
```

Zalecane wywołanie przy starcie aplikacji React lub po odświeżeniu strony; frontend odtwarza stan logowania na podstawie odpowiedzi API.

### `POST /api/auth/logout`

**Nagłówek:** `X-CSRF-TOKEN: <aktualny-token>`. Bez Body.

**Oczekiwany sukces — `200 OK`:**

```json
{ "message": "Wylogowano" }
```

Backend unieważnia sesję. Po wylogowaniu `GET /api/auth/me` powinno zwrócić `401 Unauthorized`. **Ten scenariusz wymaga jeszcze potwierdzenia w ręcznych testach zespołu.**

## Typowe statusy i błędy

| HTTP | Interpretacja | Co zrobić we frontendzie |
| --- | --- | --- |
| 200 | Poprawne wykonanie | Zaktualizować stan UI |
| 201 | Konto utworzone | Przekierować do logowania lub zalogować osobnym żądaniem |
| 400 | Błędne dane | Wyświetlić `error` przy formularzu |
| 401 | Brak sesji / złe dane logowania | Pokazać ekran logowania lub błąd formularza |
| 403 | Najczęściej brak lub niedopasowany CSRF przy żądaniu modyfikującym | Ponownie pobrać CSRF i zweryfikować cookie |
| 500 | Błąd serwera / bazy | Wyświetlić komunikat ogólny, zgłosić do backendu |

**Nie należy** wyświetlać użytkownikowi szczegółów stosu wyjątków ani przechowywać hasła w stanie globalnym dłużej niż potrzeba do wysłania formularza.

## Stan wdrożenia

Dokument opisuje przygotowaną implementację Market. Dotychczas ręcznie potwierdzono rejestrację, logowanie i `me`; wylogowanie i przypadki błędne wymagają przetestowania. Jeśli kod w repozytorium zmieni się, zaktualizuj tę dokumentację.
