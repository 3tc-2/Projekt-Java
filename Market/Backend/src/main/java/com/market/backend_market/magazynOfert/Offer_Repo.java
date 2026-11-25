package com.market.backend_market.magazynOfert;

import com.market.backend_market.magazynKlientow.User_Repo;
import com.market.backend_market.models.Offer;
import com.market.backend_market.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class Offer_Repo {

    private final JdbcTemplate jdbcTemplate;

    private final User_Repo userRepo;

    public Offer_Repo(JdbcTemplate jdbcTemplate, User_Repo userRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepo = userRepo;
    }
    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Użytkownik nie jest zalogowany");
        }

        String login = authentication.getName();
        return userRepo.findByLogin(login)
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Nie znaleziono użytkownika o loginie: " + login
                ));
    }

    private String resolveOrderBy(String sortBy, String direction) {
        String column = switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case "cena", "price" -> "cena";
            case "tytul", "title" -> "tytul";
            case "kategoria", "category" -> "kategoria";
            default -> "id_ogloszenia";
        };

        String dir = "desc".equalsIgnoreCase(direction) ? "DESC" : "ASC";
        return column + " " + dir;
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
    public List<Offer> findByTitle(String tytul) {
        String sql = """
               SELECT *
               FROM `ogloszenia`
               WHERE tytul LIKE ?
               """;
        return jdbcTemplate.query(
                sql,
                new Offer_Row_Mapper(),
                "%" + tytul + "%"
        );
    }
    // Wyszukiwanie ofert po kategorii
    public List<Offer> findByCategory(String kategoria) {
        String sql = """
               SELECT *
               FROM `ogloszenia`
               WHERE kategoria = ?
               """;
        return jdbcTemplate.query(
                sql,
                new Offer_Row_Mapper(),
                kategoria
        );
    }

    // Wyszukiwanie po id_uzytkownika
    public List<Offer> findByUserId(int id_uzytkownika) {
        String sql = """
               SELECT *
               FROM `ogloszenia`
               WHERE id_uzytkownika = ?
               """;
        return jdbcTemplate.query(
                sql,
                new Offer_Row_Mapper(),
                id_uzytkownika
        );
    }


    // Dodawanie nowej oferty
    public boolean newOffer(Offer offer, Authentication authentication) {

        long currentUserId = getCurrentUserId(authentication);

        String sql = """
               INSERT INTO `ogloszenia`
               (tytul, opis, kategoria, cena, id_uzytkownika)
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
            ps.setLong(5, currentUserId);
            return ps;}, keyHolder);

            return keyHolder.getKey() != null;
    }
    // Edycja oferty
    public boolean editOffer(Offer offer, Authentication authentication) {

        Long currentUserId = getCurrentUserId(authentication);

        String sql = """
           UPDATE `ogloszenia`
           SET tytul = ?, opis = ?, kategoria = ?, cena = ?
           WHERE id_ogloszenia = ? AND id_uzytkownika = ?;
           """;
        int rows = jdbcTemplate.update(connection ->{
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, offer.getTitle());
            ps.setString(2, offer.getDescription());
            ps.setString(3, offer.getKategory());
            ps.setDouble(4, offer.getPrice());
            ps.setLong(5, offer.getId());
            ps.setLong(6, currentUserId);

            return ps;});
        return rows > 0;
    }
    // Usuwanie ogłoszenia
    public boolean deleteOffer(Long id, Authentication authentication) {

        Long curretUserId = getCurrentUserId(authentication);

        String sql = """
            DELETE FROM `ogloszenia`
            WHERE id_ogloszenia = ? AND id_uzytkownika = ?
            """;
        int parse = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);
            ps.setLong(2, curretUserId);
            return ps;

        } );
        return parse > 0;
    }

    public List<Offer> searchOffers(
            String query,
            String category,
            Double minPrice,
            Double maxPrice,
            Long userId,
            String sortBy,
            String direction,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int offset = safePage * safeSize;

        StringBuilder sql = new StringBuilder("SELECT * FROM Ogloszenia WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            String pattern = "%" + query.trim() + "%";
            sql.append(" AND (LOWER(tytul) LIKE LOWER(?) OR LOWER(opis) LIKE LOWER(?))");
            params.add(pattern);
            params.add(pattern);
        }

        if (category != null && !category.isBlank()) {
            sql.append(" AND kategoria = ?");
            params.add(category.trim());
        }

        if (minPrice != null) {
            sql.append(" AND cena >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND cena <= ?");
            params.add(maxPrice);
        }

        if (userId != null) {
            sql.append(" AND id_uzytkownika = ?");
            params.add(userId);
        }

        sql.append(" ORDER BY ").append(resolveOrderBy(sortBy, direction));
        sql.append(" LIMIT ? OFFSET ?");
        params.add(safeSize);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), new Offer_Row_Mapper(), params.toArray());
    }

    }

