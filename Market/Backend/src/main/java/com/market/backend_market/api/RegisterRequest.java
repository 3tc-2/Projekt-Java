
package com.market.backend_market.api;

public record RegisterRequest(
        String imie,
        String nazwisko,
        String login,
        String haslo,
        String email
) {}
