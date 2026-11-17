
package com.market.backend_market.uslugi;

import com.market.backend_market.magazynKlientow.User_Repo;
import com.market.backend_market.models.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern LOGIN_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]+$"
    );

    private final User_Repo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            User_Repo userRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Long register(
            String imie,
            String nazwisko,
            String login,
            String haslo,
            String email
    ) {

        // 1. Sprawdzanie wymaganych pól
        if (imie == null || imie.isBlank()
                || nazwisko == null || nazwisko.isBlank()
                || login == null || login.isBlank()
                || email == null || email.isBlank()
                || haslo == null || haslo.isBlank()) {

            throw new IllegalArgumentException(
                    "Wszystkie pola są wymagane"
            );
        }

        // 2. Usuwanie zbędnych spacji
        imie = imie.trim();
        nazwisko = nazwisko.trim();
        login = login.trim();
        email = email.trim().toLowerCase(Locale.ROOT);

        // 3. Sprawdzanie długości
        if (imie.length() > 30) {
            throw new IllegalArgumentException(
                    "Imię może mieć maksymalnie 30 znaków"
            );
        }

        if (nazwisko.length() > 30) {
            throw new IllegalArgumentException(
                    "Nazwisko może mieć maksymalnie 30 znaków"
            );
        }

        if (login.length() < 3 || login.length() > 50) {
            throw new IllegalArgumentException(
                    "Login musi mieć od 3 do 50 znaków"
            );
        }

        if (email.length() > 50) {
            throw new IllegalArgumentException(
                    "Adres e-mail może mieć maksymalnie 50 znaków"
            );
        }

        // 4. Sprawdzanie loginu
        if (!LOGIN_PATTERN.matcher(login).matches()) {
            throw new IllegalArgumentException(
                    "Login może zawierać tylko litery, cyfry, kropki, myślniki i podkreślenia"
            );
        }

        // 5. Sprawdzanie adresu e-mail
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(
                    "Nieprawidłowy adres e-mail"
            );
        }

        // 6. Sprawdzanie hasła
        int hasloBytes = haslo.getBytes(
                StandardCharsets.UTF_8
        ).length;

        if (haslo.length() < 8 || hasloBytes > 72) {
            throw new IllegalArgumentException(
                    "Hasło musi mieć minimum 8 znaków i maksymalnie 72 bajty UTF-8"
            );
        }

        // 7. Sprawdzanie unikalności
        if (userRepo.existsByLogin(login)) {
            throw new IllegalArgumentException(
                    "Podany login jest już zajęty"
            );
        }

        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Podany adres e-mail jest już zajęty"
            );
        }

        // 8. Tworzenie użytkownika
        User user = new User();

        user.setImie(imie);
        user.setNazwisko(nazwisko);
        user.setLogin(login);
        user.setMail(email);

        // 9. Hashowanie hasła BCrypt
        user.setHaslo(passwordEncoder.encode(haslo));

        // 10. Zapis do bazy danych
        return userRepo.save(user);
    }
}
