# Projekt-Java
## Kto co robi
### Bank
BACKEND - Maciek  
BAZA DANYCH - Darek, Krystian  ZROBIONE  
FRONTEND - Emilka, Katia, sofia  
APLIKACJA MOBILNA - Julia, Basia  
### Market
BACKEND - Wiktor, Szymon  
BAZA DANYCH - Darek, Nerlo  ZROBIONE  
FRONTEND - Mikołaj  
APLIKACJA MOBILNA - Igor, Piotrek, Mati


## Diagram projektu
![diagram](Images/diagram.png)

## Struktura plików
![struktura](Images/struktura_plikow.png)

## Link do zadan
https://app.asana.com/1/1220609803856878/project/1220618715523179/board/1220618549943953

## Bank


## Market
### Baza danych
![schemat_bazy_market](Market/BazaDanych/Baza_Market_Schemat.png)
###



## Market — Dokumentacja API dla WWW i aplikacji mobilnej

## 1. Informacje podstawowe

Backend aplikacji Market został napisany w Java Spring Boot i udostępnia REST API.

**Adres backendu w sieci lokalnej:**

`http://192.168.0.174:8080`

> Backend jest dostępny tylko w sieci lokalnej, w której działa serwer. Adres może ulec zmianie.

### Uwierzytelnianie

- Logowanie odbywa się za pomocą loginu i hasła.
- Backend korzysta z sesji HTTP (`JSESSIONID`).
- Nie używamy JWT.
- Żądania zmieniające dane wymagają tokenu CSRF.
- Dane są przesyłane w formacie JSON.

---

## 2. Endpointy API

| Metoda | Endpoint | Opis | Dostęp |
|--------|----------|------|--------|
| GET | `/api/auth/csrf` | Pobranie tokenu CSRF | Publiczny |
| POST | `/api/auth/register` | Rejestracja użytkownika | Publiczny |
| POST | `/api/auth/login` | Logowanie użytkownika | Publiczny |
| GET | `/api/auth/me` | Dane zalogowanego użytkownika | Zalogowany |
| POST | `/api/auth/logout` | Wylogowanie użytkownika | Zalogowany |

### 2.1. Pobranie tokenu CSRF

**GET** `/api/auth/csrf`

Przykładowa odpowiedź:

```json
{
  "headerName": "X-CSRF-TOKEN",
  "token": "wygenerowany_token"
}
```

Token należy przekazywać w nagłówku `X-CSRF-TOKEN` przy żądaniach POST, PUT, PATCH i DELETE.

### 2.2. Rejestracja

**POST** `/api/auth/register`

Przykładowe dane:

```json
{
  "imie": "Jan",
  "nazwisko": "Kowalski",
  "login": "jan123",
  "haslo": "Test12345",
  "email": "jan@example.com"
}
```

Odpowiedź `201 Created`:

```json
{
  "message": "Użytkownik został zarejestrowany",
  "userId": 1
}
```

Wymagania:
- Wszystkie pola są obowiązkowe.
- Hasło musi zawierać minimum 8 znaków.
- Login i adres e-mail muszą być unikalne.

### 2.3. Logowanie

**POST** `/api/auth/login`

Przykładowe dane:

```json
{
  "login": "jan123",
  "haslo": "Test12345"
}
```

Odpowiedź `200 OK`:

```json
{
  "message": "Zalogowano pomyślnie",
  "userId": 1,
  "login": "jan123"
}
```

Po poprawnym logowaniu backend tworzy sesję HTTP.

### 2.4. Pobieranie profilu

**GET** `/api/auth/me`

Wymaga aktywnej sesji.

Odpowiedź `200 OK`:

```json
{
  "userId": 1,
  "login": "jan123",
  "imie": "Jan",
  "nazwisko": "Kowalski",
  "email": "jan@example.com"
}
```

### 2.5. Wylogowanie

**POST** `/api/auth/logout`

Nie wymaga danych JSON, ale wymaga aktualnego tokenu CSRF.

Odpowiedź `200 OK`:

```json
{
  "message": "Wylogowano"
}
```

Po wylogowaniu sesja zostaje unieważniona.

---

## 3. Integracja z frontendem WWW

Przy każdym żądaniu wykorzystującym sesję należy ustawić:

```javascript
credentials: "include"
```

Przykład pobrania profilu:

```javascript
const response = await fetch(
  "http://192.168.0.174:8080/api/auth/me",
  {
    credentials: "include"
  }
);

const user = await response.json();
```

Przy żądaniach POST należy dodatkowo przesłać token CSRF.

Po zalogowaniu warto ponownie pobrać CSRF, ponieważ token może zostać odnowiony.

**Uwaga:** jeżeli React działa na innym adresie IP lub porcie, konieczna może być konfiguracja CORS. Zalecane jest użycie proxy Vite dla endpointów `/api`, aby ograniczyć problemy z ciasteczkami sesyjnymi.

---

## 4. Integracja z aplikacją mobilną

Aplikacja Android/iOS powinna:

1. Pobrać token przez `GET /api/auth/csrf`.
2. Zachować ciasteczko sesyjne.
3. Wysłać żądanie logowania z tokenem CSRF.
4. Dołączać ciasteczko sesji do kolejnych żądań.
5. Po zalogowaniu odświeżyć token CSRF.
6. Przy wylogowaniu wywołać `POST /api/auth/logout`.

Nie należy przechowywać hasła użytkownika ani identyfikatora sesji w niezabezpieczonej pamięci urządzenia.

---

## 5. Kody HTTP

| Kod | Znaczenie |
|-----|-----------|
| 200 | Operacja zakończona sukcesem |
| 201 | Użytkownik został utworzony |
| 400 | Nieprawidłowe dane |
| 401 | Brak uwierzytelnienia lub błędny login/hasło |
| 403 | Brak uprawnień lub nieprawidłowy token CSRF |
| 500 | Wewnętrzny błąd serwera |

Przykładowa odpowiedź błędu:

```json
{
  "error": "Wymagane logowanie"
}
```

---

## 6. Status implementacji

- [x] Rejestracja użytkowników
- [x] Logowanie użytkowników
- [x] Pobieranie profilu
- [x] Sesje HTTP
- [x] Tokeny CSRF
- [x] Endpoint wylogowania
- [ ] Pełne testy automatyczne uwierzytelniania
- [ ] Wdrożenie produkcyjne HTTPS

Rejestracja, logowanie i pobieranie profilu zostały sprawdzone ręcznie. Endpoint wylogowania jest zaimplementowany, ale jego test nie został jeszcze potwierdzony.

---

## 7. Ważne informacje

- Backend musi być uruchomiony, żeby frontend mógł korzystać z API.
- Aktualny adres `192.168.0.174` działa w sieci lokalnej.
- Na produkcji należy korzystać z HTTPS.
- Nie należy przechowywać hasła w `localStorage`.
- Po odświeżeniu strony stan zalogowania można sprawdzić przez `GET /api/auth/me`.
- Dokumentacja obejmuje wyłącznie moduł użytkowników i uwierzytelniania. API ogłoszeń, portfeli i zamówień będzie dokumentowane osobno.



Powodzenia 3tc
