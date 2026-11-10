package com.market.backend_market.magazynOfert;

import com.market.backend_market.models.Offer;
import com.market.backend_market.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Offer_Row_Mapper implements RowMapper<Offer> {

    @Override
    public Offer mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        Offer offer = new Offer();

        offer.setId(rs.getLong("id_ogloszenia"));
        offer.setTitle(rs.getString("tytul"));
        offer.setDescription(rs.getString("opis"));
        offer.setPrice(rs.getDouble("cena"));
        offer.setKategory(rs.getString("kategoria"));
        offer.setUser_id(rs.getLong("id_uzytkownika"));


        return offer;
    }
}
