package com.bank.backend_bank.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "konta")
@Data




public class Konto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKonta;

    private String numerKonta;
    private BigDecimal saldo = BigDecimal.ZERO;

    @OneToMany(mappedBy = "konto", cascade = CascadeType.ALL)
    private List<Tranzakcja> historiaTranzakcji = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_klienta") // Klucz obcy na klienta abysmy wiedzieli do kogo nalezy konto
    private Klient klient;

    @Enumerated(EnumType.STRING) // testowalem nie wiedzialem dlaczego zapisywalo enum jako 0 1 2 okazalo sie ze trzeba wskazac aby zapisywal jako VARCHAR :/
    @Column(updatable = false)
    private TypKonta typKonta;

    public Konto(Klient klient, TypKonta typKonta) {
        if (klient == null || typKonta == null) {
            throw new IllegalArgumentException("null w jednym z obiektow ");
        }

        this.klient = klient;
        this.typKonta = typKonta;
        this.saldo = BigDecimal.ZERO;
    }

    public boolean dodajSrodki(BigDecimal kwota) {
        if (kwota != null) {
            this.saldo = this.saldo.add(kwota);
            return true;
        }
        return false;
    }

    public boolean odejmijSrodki(BigDecimal kwota) {
        if(kwota != null) {
            if(this.saldo.compareTo(kwota) < 0) { // sprawdzam 2 razy ale dobra
                return false;
            }

            this.saldo = this.saldo.subtract(kwota);
            return true;
        }
        return false;
    }

    public void dodajWychodzacaTranzakcje(Tranzakcja transakcja) {
        transakcja.setNadawca(this); // sprawdzam 2 razy
        this.historiaTranzakcji.add(transakcja);
    }

    public void dodajPrzychodzacaTranzakcje(Tranzakcja transakcja) {
        transakcja.setOdbiorca(this); // sprawdzam 2 razy
        this.historiaTranzakcji.add(transakcja);
    }
}