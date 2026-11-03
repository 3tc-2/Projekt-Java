package com.bank.backend_bank.uslugi;

import com.bank.backend_bank.magazynKlientow.bazaDanychKont;
import com.bank.backend_bank.models.Klient;
import com.bank.backend_bank.models.Konto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class KontoUslugi {


    private final bazaDanychKont bazaDanychKont;

    public KontoUslugi(bazaDanychKont bazaDanychKont) {
        this.bazaDanychKont = bazaDanychKont;
    }

    public List<String> getListaNumerowKont(Klient klient) {
        List<String> wynik = new ArrayList<>();

        List<Konto> lista =
                bazaDanychKont.findByKlient_IdKlienta(klient.getIdKlienta());

        for (Konto konto : lista) {
            if (konto != null) {
                wynik.add(konto.getNumerKonta());
            }
        }

        return wynik;
    }

    public BigDecimal getSaldoKonta(String numerKonta) {
        Konto konto = bazaDanychKont.findByNumerKonta(numerKonta)
                .orElseThrow(() -> new RuntimeException(
                        "Konto " + numerKonta + " ma wartosc null"
                ));

        return konto.getSaldo();
    }

}
