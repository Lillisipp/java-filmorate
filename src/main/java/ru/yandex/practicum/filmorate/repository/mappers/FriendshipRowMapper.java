package ru.yandex.practicum.filmorate.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Friendship(
                rs.getInt("friendId"),
                FriendshipStatus.valueOf(rs.getString("status"))
        );
    }
}
