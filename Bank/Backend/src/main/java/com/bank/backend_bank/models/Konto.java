package com.bank.backend_bank.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "konta")
@Data




public class Konto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKonta;

    private String numerKonta;
    private BigDecimal saldo = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "id_klienta") // Klucz obcy na klienta abysmy wiedzieli do kogo nalezy konto
    private Klient klient;

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
}