package ru.yandex.practicum.filmorate.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getNString("email"));
        user.setLogin(rs.getNString("login"));
        user.setName(rs.getNString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        user.setFriends(new HashSet<>());
        return user;
    }
}
