
package com.market.backend_market.magazynKlientow;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PasswordReset_Repo {

    private final JdbcTemplate jdbcTemplate;

    public PasswordReset_Repo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Long> findUserIdByEmail(String email) {
        String sql = """
                SELECT id_uzytkownika
                FROM Uzytkownicy
                WHERE email = ?
                """;

        List<Long> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("id_uzytkownika"),
                email
        );

        return results.stream().findFirst();
    }

    public void saveToken(Long userId, String tokenHash) {
        String sql = """
                INSERT INTO Tokeny_Resetowania_Hasla
                (id_uzytkownika, token_hash, data_wygasniecia)
                VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 15 MINUTE))
                """;

        jdbcTemplate.update(sql, userId, tokenHash);
    }

    public Optional<Long> findValidTokenForUpdate(String tokenHash) {
        String sql = """
                SELECT id_uzytkownika
                FROM Tokeny_Resetowania_Hasla
                WHERE token_hash = ?
                  AND data_wygasniecia > NOW()
                  AND data_wykorzystania IS NULL
                FOR UPDATE
                """;

        List<Long> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("id_uzytkownika"),
                tokenHash
        );

        return results.stream().findFirst();
    }

    public void updatePassword(Long userId, String passwordHash) {
        String sql = """
                UPDATE Uzytkownicy
                SET haslo = ?
                WHERE id_uzytkownika = ?
                """;

        int rows = jdbcTemplate.update(
                sql, passwordHash, userId
        );

        if (rows != 1) {
            throw new IllegalStateException(
                    "Nie udało się zaktualizować hasła"
            );
        }
    }

    public void invalidateUserTokens(Long userId) {
        String sql = """
                UPDATE Tokeny_Resetowania_Hasla
                SET data_wykorzystania = NOW()
                WHERE id_uzytkownika = ?
                  AND data_wykorzystania IS NULL
                """;

        jdbcTemplate.update(sql, userId);
    }
}
