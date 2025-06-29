package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Создание нового фильма: {}", film);
        validateFilm(film);
        film.setId(Utils.getNextId(films));
        films.put(film.getId(), film);
        log.debug("Фильм добавлен с ID: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Обновление отменено — ID не указан.");
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Обновление отменено — фильм с ID {} не найден.", newFilm.getId());
            throw new ConditionsNotMetException("Фильм с таким ID не найден.");
        }
        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.debug("Фильм с ID {} успешно обновлён.", newFilm.getId());
        return newFilm;
    }

    @GetMapping
    public Collection<Film> getFilm() {
        return films.values();
    }

    private void validateFilm(Film film) {
        if (film.getDuration().compareTo(Duration.ZERO) <= 0) {
            log.warn("Ошибка валидации: продолжительность не положительная: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации: дата релиза слишком ранняя: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
    }
}
