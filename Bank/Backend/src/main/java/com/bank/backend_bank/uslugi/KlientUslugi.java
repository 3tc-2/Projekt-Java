package com.bank.backend_bank.uslugi;


import com.bank.backend_bank.models.Klient;
import org.springframework.stereotype.Service;

import com.bank.backend_bank.magazynKlientow.bazaDanychKlientow;

import java.util.Random;

@Service
public class KlientUslugi { // jeszcze pracuje nad nazwa
    private final bazaDanychKlientow bazaDanychKlientow;

    public KlientUslugi(bazaDanychKlientow bazaDanychKlientow) {
        this.bazaDanychKlientow = bazaDanychKlientow;
    }

    public String wygenerujRandomowyNumer() {
        Random random = new Random();

        //https://stackoverflow.com/questions/7642656/how-to-get-a-random-between-1-100-from-randdouble-in-java

        long liczba = 1_000_000_000L + random.nextLong(9_000_000_000L);
        String wynik = String.valueOf(liczba);

        while (this.bazaDanychKlientow.czyNumerZajety(wynik)) {
            liczba = 1_000_000_000L + random.nextLong(9_000_000_000L);
            wynik = String.valueOf(liczba);
        }
        return wynik;
    }

    public Klient stworzNowegoKlienta(String pesel, String imie, String nazwisko, String mail, String tel, String haslo){
        Klient klient = new Klient(imie, nazwisko, mail, tel, pesel, this.wygenerujRandomowyNumer(), this.hashujHaslo(haslo));
        /*
            trzeba dodac warunki aby nie byl null
         */
        return klient;
    }

    private String hashujHaslo(String haslo) {
        //https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt to juz na lekcje zostawiam
        return "";
    }

}
