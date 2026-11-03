package com.bank.backend_bank.uslugi;


import com.bank.backend_bank.magazynKlientow.bazaDanychKont;
import com.bank.backend_bank.models.Konto;
import com.bank.backend_bank.models.Tranzakcja;
import jakarta.transaction.Transactional;


import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class Przelew {

    private final bazaDanychKont bazaDanych;

    Przelew(bazaDanychKont bazaDanych) {
        this.bazaDanych = bazaDanych;
    }

    // to jest taki jak w sql na Rozanie START TRANSACTION
    // Rollback to teraz jest jak rzucimy wyjatkiem czyli throw byle co tak naprawde
    // a commit to jak nie rzucimy i fukcja sie skonczy


    @Transactional
    public boolean wykonajPrzelew(Long idNadawcy, Long idOdbiorcy, BigDecimal kwota) {
        if (Objects.equals(idNadawcy, idOdbiorcy)) {
            throw new IllegalArgumentException("ID nie moga byc takie same");
        }
        Konto nadawca = this.bazaDanych.findFirstByKlient_IdKlienta(idNadawcy) // musimy uzyc naszej nowej funkcji
                .orElseThrow(() -> new IllegalArgumentException("Nieznaleziono nadawcy"));

        Konto odbiorca = this.bazaDanych.findFirstByKlient_IdKlienta(idOdbiorcy)
                .orElseThrow(() -> new IllegalArgumentException("Nieznaleziono odbiorca"));
        // (() -> new ... to jest lambda )

        //https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html#findById(ID)
        //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Optional.html#orElseThrow(java.util.function.Supplier)


        //https://www.baeldung.com/java-jpa-transaction-locks
        // ciekawe zaraz na to zobacze zastanawiam sie nad
        // zablokowaniem konta w trakcie tranzakcji dla bezpieczenstwa
        // aby nie mogl robic paru tranzakcji na raz przy tym mozliwe ze powodujac
        // minusowe saldo

        if (nadawca.getSaldo().compareTo(kwota) < 0) { // compareto daje -1 jak lewo jest wieksze niz prawo
            throw new IllegalArgumentException("Retard");
        }

        if (!nadawca.odejmijSrodki(kwota)) {
            throw new IllegalArgumentException("blad po stronie fukcji odejmij srodki (nadawca)");
        }

        if (!odbiorca.dodajSrodki(kwota)) {
            throw new IllegalArgumentException("blad po stronie fukcji dodaj srodki (odbiorca)");
        }

        Tranzakcja historia = new Tranzakcja(nadawca,odbiorca,kwota);

        nadawca.dodajWychodzacaTranzakcje(historia);
        odbiorca.dodajPrzychodzacaTranzakcje(historia);


        return true;
    }

}
