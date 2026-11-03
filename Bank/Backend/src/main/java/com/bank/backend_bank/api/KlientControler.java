package com.bank.backend_bank.api;

//https://spring.io/guides/tutorials/rest

import com.bank.backend_bank.dto.RegisterDTO;
import com.bank.backend_bank.magazynKlientow.bazaDanychKlientow;
import com.bank.backend_bank.models.Klient;
import com.bank.backend_bank.uslugi.KlientUslugi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KlientControler {

    private final bazaDanychKlientow bazaDanychKlientow;
    private final KlientUslugi klientUslugi;

    public KlientControler(bazaDanychKlientow b, KlientUslugi k) {
        this.bazaDanychKlientow = b;
        this.klientUslugi = k;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/register")
    public String rejestracja(@RequestBody RegisterDTO dane) {
        Klient klient = klientUslugi.stworzNowegoKlienta(dane);
        return klient.getNumerKlienta();
    }
}