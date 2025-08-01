package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmMapper;

    public List<Film> findAll() {
        String query = "SELECT * FROM films";
        return jdbc.query(query, filmMapper);
    }

}
