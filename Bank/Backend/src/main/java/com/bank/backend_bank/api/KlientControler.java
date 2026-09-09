package com.bank.backend_bank.api;

//https://spring.io/guides/tutorials/rest

import com.bank.backend_bank.magazynKlientow.bazaDanychKlientow;
import com.bank.backend_bank.uslugi.KlientUslugi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KlientControler {

    private final bazaDanychKlientow bazaDanychKlientow;

    public KlientControler(bazaDanychKlientow b) { this.bazaDanychKlientow = b; }



    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
