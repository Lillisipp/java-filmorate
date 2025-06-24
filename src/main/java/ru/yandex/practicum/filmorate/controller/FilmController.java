package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/film")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    private static final int MAX_DESC_LENGTH = 200;


    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        film.setId(Utils.getNextId(films));
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
        if (!films.containsKey(newFilm.getId())) {
            throw new ConditionsNotMetException("Пользователь с таким ID не найден.");
        }
        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @GetMapping
    public Collection<Film> getFilm() {
        return films.values();
    }

    private void validateFilm(Film film) {
        if (!StringUtils.hasText(film.getName()) || film.getName().contains(" ")) {
            throw new ValidationException("название не может быть пустым");
        }
        if (!StringUtils.hasText(film.getName()) || film.getDescription().length() > MAX_DESC_LENGTH) {
            throw new ValidationException("максимальная длина описания — 200 символов.");
        }
        if (film.getDuration().compareTo(Duration.ZERO) >= 0) {
            throw new ValidationException("продолжительность фильма должна быть положительным числом.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 11, 28))) {
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года.");
        }
    }
}
