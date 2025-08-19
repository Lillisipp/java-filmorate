package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getFilms();

    Optional<Film> getFilmById(Integer id);

    Film save(Film film);

    Film update(Film newFilm);

    void likeFilm(int filmId, int userId);

    void removeLikeFilm(int filmId, int userId);

    List<Film> topLikeFilm(int count);

    boolean exist(Film film);

    List<Genre> getGenres();

    Optional<Genre> getGenreById(int id);

    List<MpaRating> getMpa();

    Optional<MpaRating>  getMPAById(int id);
}
