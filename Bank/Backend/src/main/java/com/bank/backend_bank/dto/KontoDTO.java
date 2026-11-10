package com.bank.backend_bank.dto;

import com.bank.backend_bank.models.Konto;

import java.math.BigDecimal;

public record KontoDTO(
        String numerKonta,
        BigDecimal saldo,
        String typKonta
) {

    public static KontoDTO from(Konto konto) {
        return new KontoDTO(
                konto.getNumerKonta(),
                konto.getSaldo(),
                konto.getTypKonta().name()
        );
    }
}