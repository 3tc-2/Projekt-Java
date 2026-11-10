package com.bank.backend_bank.uslugi;

import com.bank.backend_bank.dto.KontoDTO;
import com.bank.backend_bank.magazynKlientow.bazaDanychKont;
import com.bank.backend_bank.models.Klient;
import com.bank.backend_bank.models.Konto;
import com.bank.backend_bank.models.TypKonta;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
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
                        "Nie znaleziono konta " + numerKonta
                ));

        return konto.getSaldo();
    }

    // NOWA METODA
    public List<KontoDTO> getMojeKonta(Klient klient) {

        List<Konto> konta =
                bazaDanychKont.findByKlient_IdKlienta(klient.getIdKlienta());

        return konta.stream()
                .map(KontoDTO::from)
                .toList();
    }


    private final SecureRandom random = new SecureRandom();

    private String generujNumerKonta() {

        while (true) {

            // 8 cyfr numeru rozliczeniowego banku
            String numerBanku = "12345678";

            // 16 losowych cyfr rachunku
            StringBuilder numerRachunku = new StringBuilder();

            for (int i = 0; i < 16; i++) {
                numerRachunku.append(random.nextInt(10));
            }

            // Numer NRB bez dwóch cyfr kontrolnych
            String numerBezKontrolnych = numerBanku + numerRachunku;

            // Obliczanie cyfr kontrolnych zgodnie z NRB
            String doObliczenia = numerBezKontrolnych + "252100";

            BigInteger liczba = new BigInteger(doObliczenia);

            int reszta = liczba.mod(BigInteger.valueOf(97)).intValue();

            int cyfryKontrolne = 98 - reszta;

            String numerKonta = String.format(
                    "%02d%s",
                    cyfryKontrolne,
                    numerBezKontrolnych
            );

            // Sprawdzamy czy numer już istnieje
            if (bazaDanychKont.findByNumerKonta(numerKonta).isEmpty()) {
                return numerKonta;
            }
        }
    }

    public Konto dodajKonto(Klient klient, TypKonta typKonta) {

        List<Konto> lista =
                bazaDanychKont.findByKlient_IdKlienta(klient.getIdKlienta());

        if (lista.size() >= 3) {
            throw new IllegalArgumentException(
                    "Klient może posiadać maksymalnie 3 konta"
            );
        }

        Konto konto = new Konto(klient, typKonta);

        konto.setNumerKonta(generujNumerKonta());

        return bazaDanychKont.save(konto);
    }
}