
package com.market.backend_market.uslugi;

import com.market.backend_market.magazynKlientow.UserRepo;
import com.market.backend_market.models.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Rejestracja użytkownika
    public Long register(
            String imie,
            String nazwisko,
            String login,
            String haslo,
            String email
    ) {

        if (userRepo.existsByLogin(login)) {
            throw new IllegalArgumentException(
                    "Podany login jest już zajęty"
            );
        }

        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Podany adres email jest już zajęty"
            );
        }

        User user = new User();

        user.setImie(imie);
        user.setNazwisko(nazwisko);
        user.setLogin(login);

        // Hashowanie hasła
        user.setHaslo(passwordEncoder.encode(haslo));

        user.setMail(email);

        return userRepo.save(user);
    }
}
