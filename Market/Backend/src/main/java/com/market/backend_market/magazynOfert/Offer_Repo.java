package com.market.backend_market.magazynOfert;

import com.market.backend_market.magazynKlientow.User_Row_Mapper;
import com.market.backend_market.models.Offer;
import com.market.backend_market.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class Offer_Repo {


    private final JdbcTemplate jdbcTemplate;
    public Offer_Repo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    // Wyszukiwanie Oferty po ID
    public Optional<Offer> findById(Long id) {
        String sql = """
               SELECT *
               FROM Ogloszenia
               WHERE id_ogloszenia = ?
               """;
        return jdbcTemplate.query(
                sql,
                new Offer_Row_Mapper(),
                id
        ).stream().findFirst();
    }
    // Wyszukiwanie ofert po tytule
    public Optional<Offer> findByTitle(String tytul) {
        String sql = """
               SELECT *
               FROM `Uzytkownicy`
               WHERE tytul = ?
               """;
        return jdbcTemplate.query(
                sql,
                new Offer_Row_Mapper(),
                tytul
        ).stream().findFirst();
    }
    // Wyszukiwanie ofert po kategorii
    public Optional<Offer> findByLogin(String kategoria) {
        String sql = """
               SELECT *
               FROM `ogloszenia`
               WHERE kategoria = ?
               """;
        return jdbcTemplate.query(
                sql,
                new Offer_Row_Mapper(),
                kategoria
        ).stream().findFirst();
    }

    // Wyszukiwanie po id_uzytkownika
    public Optional<Offer> existsByUserId(String id_uzytkownika) {
        String sql = """
               SELECT *
               FROM `ogloszenia`
               WHERE id_uzytkownika = ?
               """;
        return jdbcTemplate.query(
                sql,
                new Offer_Row_Mapper(),
                id_uzytkownika
        ).stream().findFirst();

    }
    // Dodawanie nowej oferty
    public boolean newOffer(Offer offer) {
        String sql = """
               INSERT INTO `ogloszenia`
               (tytul, opis, kategoria, cena, id_uztkownika)
               VALUES (?, ?, ?, ?, ?)
               """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, offer.getTitle());
            ps.setString(2, offer.getDescription());
            ps.setString(3, offer.getKategory());
            ps.setDouble(4, offer.getPrice());
            ps.setLong(5, offer.getUser_id());
            return ps;}, keyHolder);
        if (keyHolder.getKey() == null) {
            return false;
        }else {
        return keyHolder.getKey().equals(offer.getUser_id());
        }
    }
    // Edycja oferty
    public boolean editOffer(Offer offer) {
        String sql = """
           UPDATE `ogloszenia`
           SET opis = ?, description = ?, kategory = ?, cena = ?
           WHERE id_ogloszenia = ?;
           """;
        int rows = jdbcTemplate.update(connection ->{
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, offer.getTitle());
            ps.setString(2, offer.getDescription());
            ps.setString(3, offer.getKategory());
            ps.setDouble(4, offer.getPrice());
            return ps;});
        if(rows < 0) {
            throw new IllegalStateException(
                    "Aktualizaja nie powiodła się"
            );
        }else{
            return true;
        }
    }
    // Usuwanie ogłoszenia
    public boolean deleteOffer(Long id) {
        String sql = """
            DELETE FROM `ogloszenia`
            WHERE id_ogloszenia = ?
            """;
        int parse = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);
            return ps;

        } );
        if (parse < 0) {
            throw new IllegalStateException(
                    "Nie udało się usunąć oferty"
            );
        }else {
            return true;
        }
    }

    }

