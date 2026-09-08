package com.bank.backend_bank.models;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tranzakcje")
@Data
@NoArgsConstructor

public class Tranzakcja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTranzacji;


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
