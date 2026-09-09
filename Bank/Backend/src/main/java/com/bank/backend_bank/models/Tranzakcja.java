package com.bank.backend_bank.models;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tranzakcje")
@Getter
@Setter
@NoArgsConstructor

//https://medium.com/@cemdrman/the-n-1-you-didnt-write-lombok-data-on-jpa-entities-1e19848e1b21
// nie uzywam @Data tutaj

public class Tranzakcja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTranzakcji;


    @ManyToOne
    @JoinColumn(name = "id_konta_nadawcy", nullable = false)
    private Konto nadawca;

    @ManyToOne
    @JoinColumn(name = "id_konta_odbiorcy", nullable = false)
    private Konto odbiorca;

    @Column(updatable = false)
    private LocalDateTime czasTranzakcji = LocalDateTime.now();

    @Column(updatable = false)
    private BigDecimal kwota;

    public Tranzakcja(Konto odbiorca, Konto nadawca, BigDecimal kwota) {
        this.odbiorca = odbiorca;
        this.nadawca = nadawca;
        this.kwota = kwota;
    }
}
