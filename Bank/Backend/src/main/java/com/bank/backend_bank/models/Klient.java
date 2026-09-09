package com.bank.backend_bank.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "klienci")
@Data // automatyczne getter setter porownywanie

// wiecej informacji o tym jest tutaj https://jakarta.ee/specifications/persistence/3.1/apidocs/allclasses


public class Klient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, unique = true)
    private Long idKlienta;

    @Column(nullable = false, unique = true, updatable = false, length = 10)
    private String numerKlienta;

    @Column(nullable = false)
    private String imie;
    @Column(nullable = false)
    private String nazwisko;


    @Column(nullable = false, unique = true)
    private String mail;

    @Column(nullable = false, unique = true, updatable = false)
    private String tel;

    @Column(nullable = false, length = 150)
    private String hasloHash;

    @Column(nullable = false, unique = true, updatable = false)
    private String pesel;




    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Konto> konta = new ArrayList<>();

    public Klient() {
        throw new IllegalArgumentException("Nie podano argumentow do stworzenia Klient");
    }

    public Klient(String imie, String nazwisko, String mail, String tel, String pesel, String numerKlienta, String hasloHash) {
        if (!isValidPesel(pesel)) {
            throw new IllegalArgumentException("Zly pesel " + pesel); // https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/IllegalArgumentException.html
            /*
            Jak mozna se poradzic z throw poradnik :
                https://www.w3schools.com/java/java_try_catch.asp
            Oraz jak rzucac wyjatkami :
                https://www.geeksforgeeks.org/java/throw-throws-java/
             */
        }

        this.imie = imie;
        this.nazwisko = nazwisko;
        this.pesel = pesel;
        this.numerKlienta = numerKlienta;
        this.hasloHash = hasloHash;

        // tego brakowalo
        this.mail = mail;
        this.tel = tel;

    }

    public boolean dodajKonto(TypKonta typKonta) {
        try {

            Konto noweKonto = new Konto(this, typKonta);
            noweKonto.setKlient(this);
            this.konta.add(noweKonto);
            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("Podano null do jednego z argumentow");
            return false;
        }
    }
    public static boolean isValidPesel(String pesel) {

        // pesel ma 11 cyfr
        if (pesel == null || pesel.length() != 11) {
            return false;
        }

        /*
            sprawdzamy czy wszystkie znaki to cyfry poniewaz pesel nie moze posiadac liter oraz znakow specjalnych
         */

        for (char znak : pesel.toCharArray()) {
            if (!Character.isDigit(znak)) {
                return false;
            }
        }

        int[] waga = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int suma = 0;

        for (int i = 0; i < 10; i++) {
            int cyfra = Character.getNumericValue(pesel.charAt(i));
            suma += cyfra * waga[i];
        }

        // liczba kontrolna (10 - (suma % 10)) % 10
        int liczbaKontrolna = (10 - (suma % 10)) % 10;
        int ostatnia = Character.getNumericValue(pesel.charAt(10));

        return liczbaKontrolna == ostatnia;
    }

    public BigDecimal dodajSaldoKont() {
        BigDecimal suma = BigDecimal.ZERO;
        for (Konto konto : this.konta) {
            suma = suma.add(konto.getSaldo());
        }
        return suma;
    }

}