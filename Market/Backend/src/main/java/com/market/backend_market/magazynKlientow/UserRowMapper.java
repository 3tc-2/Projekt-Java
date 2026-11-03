
package com.market.backend_market.magazynKlientow;

import com.market.backend_market.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        User user = new User();

        user.setId(rs.getLong("id_uzytkownika"));
        user.setImie(rs.getString("imie"));
        user.setNazwisko(rs.getString("nazwisko"));
        user.setLogin(rs.getString("login"));
        user.setHaslo(rs.getString("haslo"));
        user.setMail(rs.getString("email"));

        return user;
    }
}
