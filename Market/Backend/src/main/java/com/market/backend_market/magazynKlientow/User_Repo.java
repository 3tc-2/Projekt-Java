
package com.market.backend_market.magazynKlientow;

import com.market.backend_market.models.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class User_Repo {

    private final JdbcTemplate jdbcTemplate;

    public User_Repo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Wyszukiwanie użytkownika po ID
    public Optional<User> findById(Long id) {

        String sql = """
                SELECT *
                FROM Uzytkownicy
                WHERE id_uzytkownika = ?
                """;

        return jdbcTemplate.query(
                sql,
                new User_Row_Mapper(),
                id
        ).stream().findFirst();
    }

    // Wyszukiwanie użytkownika po loginie
    public Optional<User> findByLogin(String login) {

        String sql = """
                SELECT *
                FROM `Uzytkownicy`
                WHERE login = ?
                """;

        return jdbcTemplate.query(
                sql,
                new User_Row_Mapper(),
                login
        ).stream().findFirst();
    }

    // Sprawdzanie, czy login już istnieje
    public boolean existsByLogin(String login) {

        String sql = """
                SELECT COUNT(*)
                FROM `uzytkownicy`
                WHERE login = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                login
        );

        return count != null && count > 0;
    }

    // Dodawanie nowego użytkownika
    public Long save(User user) {

        String sql = """
                INSERT INTO `Uzytkownicy`
                (imie, nazwisko, login, haslo, email)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, user.getImie());
            ps.setString(2, user.getNazwisko());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getHaslo());
            ps.setString(5, user.getMail());

            return ps;

        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new IllegalStateException(
                    "Nie udało się pobrać ID nowego użytkownika"
            );
        }

        return keyHolder.getKey().longValue();
    }

    // Sprawdzanie, czy email już istnieje
    public boolean existsByEmail(String email) {

        String sql = """
            SELECT COUNT(*)
            FROM Uzytkownicy
            WHERE email = ?
            """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                email
        );

        return count != null && count > 0;
    }
}
