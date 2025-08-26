package ru.yandex.practicum.filmorate.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.enums.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRatingRowMapper implements RowMapper<MpaRating> {
    @Override
    public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(rs.getInt("mpa_id"));
        mpaRating.setMpa(MPA.fromName(rs.getString("name")));
        return mpaRating;
    }
}
