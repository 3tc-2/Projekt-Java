package com.bank.backend_bank.api;

//https://spring.io/guides/tutorials/rest

import com.bank.backend_bank.dto.DodajKontoDTO;
import com.bank.backend_bank.models.Konto;
import com.bank.backend_bank.models.TypKonta;
import com.bank.backend_bank.uslugi.KontoUslugi;
import org.springframework.security.core.Authentication;
import com.bank.backend_bank.dto.RegisterDTO;
import com.bank.backend_bank.magazynKlientow.bazaDanychKlientow;
import com.bank.backend_bank.models.Klient;
import com.bank.backend_bank.uslugi.KlientUslugi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KlientControler {

    private final bazaDanychKlientow bazaDanychKlientow;
    private final KlientUslugi klientUslugi;
    private final KontoUslugi kontoUslugi;

    public KlientControler(
            bazaDanychKlientow b,
            KlientUslugi k,
            KontoUslugi kontoUslugi
    ) {
        this.bazaDanychKlientow = b;
        this.klientUslugi = k;
        this.kontoUslugi = kontoUslugi;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/register")
    public ResponseEntity<?> rejestracja(@RequestBody RegisterDTO dane) {

        try {
            Klient klient = klientUslugi.stworzNowegoKlienta(dane);

            return ResponseEntity
                    .ok(klient.getNumerKlienta());

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity
                    .status(401)
                    .body("Nie jesteś zalogowany");
        }

        Klient klient = bazaDanychKlientow
                .findByNumerKlienta(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Zalogowano pomyślnie",
                        "numerKlienta", klient.getNumerKlienta(),
                        "imie", klient.getImie(),
                        "nazwisko", klient.getNazwisko()
                )
        );
    }

    @GetMapping("/moj-profil")
    public ResponseEntity<?> mojProfil(Authentication authentication) {

        Klient klient = bazaDanychKlientow
                .findByNumerKlienta(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                java.util.Map.of(
                        "numerKlienta", klient.getNumerKlienta(),
                        "imie", klient.getImie(),
                        "nazwisko", klient.getNazwisko(),
                        "email", klient.getMail()
                )
        );
    }

    @GetMapping("/moje-konta")
    public ResponseEntity<?> mojeKonta(Authentication authentication) {

        Klient klient = bazaDanychKlientow
                .findByNumerKlienta(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                kontoUslugi.getMojeKonta(klient)
        );
    }



    @PostMapping("/dodajKonto")
    public ResponseEntity<?> dodajKonto(
            @RequestBody java.util.Map<String, String> dane,
            Authentication authentication
    ) {

        try {
            TypKonta typKonta = TypKonta.valueOf(dane.get("typKonta"));

            Klient klient = bazaDanychKlientow
                    .findByNumerKlienta(authentication.getName())
                    .orElseThrow();

            Konto konto = kontoUslugi.dodajKonto(klient, typKonta);

            return ResponseEntity.ok(
                    java.util.Map.of(
                            "message", "Konto zostało utworzone",
                            "numerKonta", konto.getNumerKonta(),
                            "typKonta", konto.getTypKonta().name(),
                            "saldo", konto.getSaldo()
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }


}