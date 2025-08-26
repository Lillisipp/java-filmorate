package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Qualifier("dbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private final GenreRowMapper mapperGenre;
    private final MpaRatingRowMapper mapperMpa;

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String SAVE_FILM =
            "INSERT INTO films(name, description, release_date, duration_min, mpa_id) VALUES (?,?,?,?,?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String UPDATE_BY_FILM = """
            UPDATE films
               SET name=?,
                   description=?,
                   release_date=?,
                   duration_min=?,
                   mpa_id=?
             WHERE film_id=?
            """;
    private static final String LIKE_FILM =
            "INSERT INTO film_likes(film_id, user_id) VALUES (?,?)";
    private static final String REMOVE_LIKE =
            "DELETE FROM film_likes WHERE film_id=? AND user_id=?";
    private static final String GET_TOP_LIKE_FILM = """
            SELECT f.*
            FROM films f LEFT JOIN film_likes l ON f.film_id = l.film_id
            GROUP BY f.film_id
            ORDER BY COUNT(l.user_id) DESC
             LIMIT ?
            """;
    private static final String CHECK_EXIST =
            "SELECT COUNT(*) FROM films WHERE film_id = ?";

    private static final String GET_GENRES = "SELECT genre_id, name FROM genres ORDER BY genre_id";
    private static final String GET_GENRES_BY_ID = "SELECT genre_id, name FROM genres WHERE genre_id=?";
    private static final String GET_MPA_BY_ID = "SELECT mpa_id, name FROM mpa_rating WHERE mpa_id=?";
    private static final String GET_MPA = "SELECT mpa_id, name FROM mpa_rating ORDER BY mpa_id";

    @Override
    public Collection<Film> getFilms() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film save(Film film) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(SAVE_FILM, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, (int) film.getDuration().toMinutes());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, kh);
        film.setId(Objects.requireNonNull(kh.getKey()).intValue());

        int newId = Objects.requireNonNull(kh.getKey()).intValue();
        if (!film.getGenres().isEmpty()) {
            String sqlGenre = "INSERT INTO film_genres(film_id, genre_id) VALUES (?,?)";
            for (Genre g : film.getGenres()) {
                jdbc.update(sqlGenre, newId, g.getId());
            }
        }
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        int rows = jdbc.update(UPDATE_BY_FILM,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                (int) newFilm.getDuration().toMinutes(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        if (rows == 0) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", newFilm.getId());//, newFilm.getId());
        for (Genre g : newFilm.getGenres()) {
            jdbc.update("INSERT INTO film_genres(film_id, genre_id) VALUES (?,?)",
                    newFilm.getId(), g.getId());
        }
        return newFilm;
    }

    @Override
    public void likeFilm(int filmId, int userId) {
        jdbc.update(LIKE_FILM, filmId, userId);
    }

    @Override
    public void removeLikeFilm(int filmId, int userId) {
        jdbc.update(REMOVE_LIKE, filmId, userId);
    }

    @Override
    public List<Film> topLikeFilm(int count) {
        return jdbc.query(GET_TOP_LIKE_FILM, mapper, count);
    }

    @Override
    public boolean exist(Film film) {
        Integer count = jdbc.queryForObject(CHECK_EXIST, Integer.class, film.getId());
        return count != null && count > 0;
    }

    @Override
    public List<Genre> getGenres() {
        return jdbc.query(GET_GENRES, mapperGenre);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        return jdbc.query(GET_GENRES_BY_ID, mapperGenre, id)
                .stream().findFirst();
    }

    @Override
    public List<MpaRating> getMpa() {
        return jdbc.query(GET_MPA, mapperMpa);
    }

    @Override
    public Optional<MpaRating> getMPAById(int id) {
        return jdbc.query(GET_MPA_BY_ID, mapperMpa, id)
                .stream()
                .findFirst();
    }
}
