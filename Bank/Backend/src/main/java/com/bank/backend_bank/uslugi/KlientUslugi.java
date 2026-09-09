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

    private PasswordEncoder passwordEncoder = new PasswordEncoder();


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

        /*
            trzeba dodac warunki aby nie byl null
         */
        return new Klient(imie, nazwisko, mail, tel, pesel, this.wygenerujRandomowyNumer(), this.hashujHaslo(haslo));
    }

    private String hashujHaslo(String haslo) {
        //https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt to juz na lekcje zostawiam

        return passwordEncoder.encode(haslo);
    }


    public Klient zalogujNaKlient(String numerKlienta, String rawPassword) {
        Klient klient =  bazaDanychKlientow.findByNumerKlienta(numerKlienta);
        if (klient == null) {
            throw new RuntimeException("Konto " + numerKlienta + " ma wartosc null");
        }
        if (!passwordEncoder.matches(rawPassword, klient.getHasloHash())) {
            throw new RuntimeException("Niepoprawny haslo do konta");
        }
        return klient;
    }



}
