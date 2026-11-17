# Market — testowanie API w Postmanie

## Wymagania

- Działający backend na `http://localhost:8080`.
- Uruchomiona baza MySQL/MariaDB, poprawne `application.properties`.
- Postman z włączoną obsługą cookies dla `localhost`.

## Konfiguracja kolekcji

1. Utwórz kolekcję **Market Backend**.
2. Dodaj żądania: `Pobierz CSRF`, `Rejestracja`, `Logowanie`, `Mój profil`, `Wylogowanie`.
3. W kolekcji dodaj zmienną `baseUrl` = `http://localhost:8080`.
4. W URL używaj `{{baseUrl}}/api/auth/...`.
5. W `Pobierz CSRF` ustaw metodę `GET` i URL `{{baseUrl}}/api/auth/csrf`.
6. W zakładce **Scripts → Post-response** (`Tests` w starszej wersji Postmana) wpisz:

```javascript
const csrf = pm.response.json();
pm.collectionVariables.set("csrfToken", csrf.token);
```

7. W żądaniach POST dodaj nagłówek `X-CSRF-TOKEN` o wartości `{{csrfToken}}`.
8. Przy JSON dodaj `Content-Type: application/json` lub wybierz **Body → raw → JSON**.
9. W zakładce **Authorization** wybierz **No Auth** (sesja działa poprzez cookie, nie bearer token).

## Kolejność testów

| Nr | Żądanie | Body | Oczekiwane HTTP |
| --- | --- | --- | --- |
| 1 | `GET {{baseUrl}}/api/auth/csrf` | — | 200 |
| 2 | `POST {{baseUrl}}/api/auth/register` | JSON rejestracji | 201 |
| 3 | `POST {{baseUrl}}/api/auth/register` (ponownie te same dane) | JSON rejestracji | 400 |
| 4 | `POST {{baseUrl}}/api/auth/login` | JSON logowania | 200 |
| 5 | `GET {{baseUrl}}/api/auth/me` | — | 200 |
| 6 | `GET {{baseUrl}}/api/auth/csrf` (odśwież token po logowaniu) | — | 200 |
| 7 | `POST {{baseUrl}}/api/auth/logout` | — | 200 (do potwierdzenia) |
| 8 | `GET {{baseUrl}}/api/auth/me` | — | 401 (do potwierdzenia) |

### Rejestracja — Body

```json
{
  "imie": "Jan",
  "nazwisko": "Kowalski",
  "login": "jan_test_123",
  "haslo": "Test12345",
  "email": "jan_test_123@example.com"
}
```

Jeśli taki użytkownik już istnieje, wybierz inny login i e-mail.

### Logowanie — Body

```json
{
  "login": "jan_test_123",
  "haslo": "Test12345"
}
```

## Cookies

Postman powinien zachowywać `JSESSIONID` między żądaniami do tej samej domeny. Sprawdź to przyciskiem **Cookies** przy żądaniu. Nie wpisuj ręcznie nagłówka `Cookie`, jeśli Postman robi to automatycznie. Po logowaniu użyj ponownie `GET /csrf`, żeby odświeżyć token.

## Weryfikacja bazy

```sql
USE Market_Baza;
SELECT id_uzytkownika, login, email, haslo
FROM Uzytkownicy
WHERE login = 'jan_test_123';
```

Hasło powinno być hashem BCrypt, nie jawnym tekstem `Test12345`. Nie kopiuj całego hasha do zgłoszeń, logów ani zrzutów ekranu.

## Diagnostyka

| Objaw | Weryfikacja |
| --- | --- |
| `ECONNREFUSED localhost:8080` | Uruchom Spring Boot; sprawdź port |
| `Connection refused` w sterowniku JDBC | Uruchom MySQL/MariaDB, sprawdź port bazy i URL JDBC |
| `403 Forbidden` przy POST | Pobierz aktualny CSRF, sprawdź `JSESSIONID` i nagłówek |
| `401 Unauthorized` przy `GET /me` | Sprawdź wcześniejsze logowanie oraz ciasteczko sesji |
| `401` przy loginie | Zweryfikuj login, hasło oraz hash BCrypt użytkownika |
| `400` przy rejestracji | Sprawdź komunikat `error`, duplikaty loginu/e-maila i wymagane pola |
| `500` | Sprawdź log Spring Boot i połączenie z bazą |

## Stan potwierdzenia

Z dotychczasowych testów zespołu: udana rejestracja, logowanie i odczyt profilu. **Wylogowanie, dostęp po wylogowaniu oraz scenariusze błędne powinny zostać wykonane i odnotowane przed zakończeniem implementacji.**
