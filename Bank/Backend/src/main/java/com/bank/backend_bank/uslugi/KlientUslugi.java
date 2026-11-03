package com.bank.backend_bank.uslugi;


import com.bank.backend_bank.dto.RegisterDTO;
import com.bank.backend_bank.models.Klient;
import com.bank.backend_bank.zabezpieczenia.KoderHasla;
import org.springframework.stereotype.Service;
import com.bank.backend_bank.magazynKlientow.bazaDanychKlientow;
import java.util.Random;


@Service
public class KlientUslugi { // jeszcze pracuje nad nazwa

    private final bazaDanychKlientow bazaDanychKlientow;
    private final KoderHasla passwordEncoder;

    public KlientUslugi(bazaDanychKlientow bazaDanychKlientow, KoderHasla passwordEncoder) { //podobno spring automatycznie przekazuje te rzeczy jak jest @configuration czy jakos tak
        this.bazaDanychKlientow = bazaDanychKlientow;
        this.passwordEncoder = passwordEncoder;
    }

    public String wygenerujRandomowyNumer() {
        Random random = new Random();

        //https://stackoverflow.com/questions/7642656/how-to-get-a-random-between-1-100-from-randdouble-in-java

        long liczba = 1_000_000_000L + random.nextLong(9_000_000_000L);
        String wynik = String.valueOf(liczba);

        while (this.bazaDanychKlientow.existsByNumerKlienta(wynik)) {
            liczba = 1_000_000_000L + random.nextLong(9_000_000_000L);
            wynik = String.valueOf(liczba);
        }
        return wynik;
    }

    public Klient stworzNowegoKlienta(RegisterDTO registerDTO) {


        if (registerDTO.getPesel() == null || registerDTO.getName() == null || registerDTO.getSurname() == null || registerDTO.getEmail() == null || registerDTO.getTelefon() == null || registerDTO.getPassword() == null) {
            throw new IllegalArgumentException("W jednym z argumentow znajduje sie null");
        }

        Klient klient = new Klient(registerDTO.getName(), registerDTO.getSurname(), registerDTO.getEmail(), registerDTO.getTelefon(), registerDTO.getPesel(), this.wygenerujRandomowyNumer(), this.hashujHaslo(registerDTO.getPassword()));

        return bazaDanychKlientow.save(klient); // okazalo sie ze to zapisuje w bazie danych i odrazu wzraca go
    }

    private String hashujHaslo(String haslo) {
        //https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt to juz na lekcje zostawiam

        return passwordEncoder.encode(haslo);
    }


    public Klient zalogujNaKlient(String numerKlienta, String rawPassword) {
        Klient klient = bazaDanychKlientow.findByNumerKlienta(numerKlienta)
                .orElseThrow(() -> new RuntimeException(
                        "Konto " + numerKlienta + " ma wartosc null"
                ));

        if (!passwordEncoder.matches(rawPassword, klient.getHasloHash())) {
            throw new RuntimeException("Niepoprawny haslo do konta");
        }

        return klient;
    }



}
