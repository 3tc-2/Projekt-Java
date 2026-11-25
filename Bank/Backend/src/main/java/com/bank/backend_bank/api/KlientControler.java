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

    @GetMapping(value = "/test", produces = "text/html")
    public String test() {
        return """
            <!DOCTYPE html>
            <html lang="pl">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Bank API - Dokumentacja</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background: #f4f6f9;
                        margin: 0;
                        padding: 30px;
                        color: #333;
                    }

                    h1 {
                        color: #173b68;
                    }

                    .container {
                        max-width: 900px;
                        margin: auto;
                    }

                    .endpoint {
                        background: white;
                        padding: 20px;
                        margin: 15px 0;
                        border-radius: 8px;
                        box-shadow: 0 2px 5px #ddd;
                    }

                    .method {
                        display: inline-block;
                        padding: 5px 10px;
                        border-radius: 4px;
                        color: white;
                        font-weight: bold;
                        margin-right: 10px;
                    }

                    .get {
                        background: #2196F3;
                    }

                    .post {
                        background: #4CAF50;
                    }

                    code {
                        background: #eee;
                        padding: 4px 8px;
                        border-radius: 4px;
                    }

                    .description {
                        color: #666;
                    }
                </style>
            </head>

            <body>
                <div class="container">

                    <h1>Bank API - Dokumentacja</h1>
                    <p>Dokumentacja endpointów REST API aplikacji bankowej.</p>

                    <h2>Endpointy klienta</h2>

                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/test</code>
                        <p class="description">
                            Wyświetla dokumentację API aplikacji bankowej.
                        </p>
                    </div>

                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/register</code>
                        <p>Rejestruje nowego klienta banku.</p>
                        <p><b>Request Body:</b></p>
                        <code>RegisterDTO</code>
                        <p><b>Odpowiedź:</b> Numer klienta.</p>
                    </div>

                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/login</code>
                        <p>Loguje klienta i zwraca podstawowe informacje o jego koncie.</p>
                        <p><b>Wymaga uwierzytelnienia Spring Security.</b></p>
                    </div>

                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/moj-profil</code>
                        <p>Pobiera dane zalogowanego klienta.</p>
                        <p><b>Zwracane dane:</b> numer klienta, imię, nazwisko, email.</p>
                        <p><b>Wymaga uwierzytelnienia.</b></p>
                    </div>

                    <div class="endpoint">
                        <span class="method get">GET</span>
                        <code>/moje-konta</code>
                        <p>Pobiera listę kont bankowych zalogowanego klienta.</p>
                        <p><b>Wymaga uwierzytelnienia.</b></p>
                    </div>

                    <div class="endpoint">
                        <span class="method post">POST</span>
                        <code>/dodajKonto</code>
                        <p>Tworzy nowe konto bankowe dla zalogowanego klienta.</p>

                        <p><b>Request Body:</b></p>
                        <pre>{
    "typKonta": "OSOBISTE"
}</pre>

                        <p><b>Odpowiedź:</b> Komunikat, numer konta, typ konta i saldo.</p>
                        <p><b>Wymaga uwierzytelnienia.</b></p>
                    </div>

                    <hr>

                    <p style="text-align:center;color:#888">
                        Bank Backend API | Dokumentacja REST
                    </p>

                </div>
            </body>
            </html>
            """;
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