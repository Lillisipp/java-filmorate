package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film save(Film film) {
        film.setId(Utils.getNextId(films));
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public void likeFilm(int filmId, int userId) {
        Film film = getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
        film.getLikes().add(userId);
    }


    @Override
    public void removeLikeFilm(int filmId, int userId) {
        Film film = getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
        if (!film.getLikes().contains(userId)) {
            throw new ConditionsNotMetException("Лайк от пользователя не найден.");
        }
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> topLikeFilm(int count) {
        return films
                .values()
                .stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exist(Film film) {
        return films.containsKey(film.getId());
    }

    @Override
    public List<Genre> getGeners() {
        return List.of();
    }

    @Override
    public Optional<Genre> getGenerById(int id) {
        return Optional.empty();
    }

    @Override
    public List<MpaRating> getMpa() {
        return List.of();
    }

    @Override
    public Optional<MpaRating> getMPAById(int id) {
        return Optional.empty();
    }
}
