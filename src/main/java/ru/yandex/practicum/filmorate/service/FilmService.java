package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film addFilm(Film film) {
        log.debug("Создание нового фильма: {}", film);
        validateFilm(film);
        filmStorage.save(film);
        log.debug("Фильм добавлен с ID: {}", film.getId());
        return film;
    }

    public Film updateFilm(Film updatedFilm) {
        if (updatedFilm.getId() == null) {
            log.warn("Обновление отменено — ID не указан.");
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
        if (!filmStorage.exist(updatedFilm)) {
            log.warn("Обновление отменено — фильм с ID {} не найден.", updatedFilm.getId());
            throw new ConditionsNotMetException("Фильм с таким ID не найден.");
        }
        validateFilm(updatedFilm);
        filmStorage.update(updatedFilm);
        log.debug("Фильм с ID {} успешно обновлён.", updatedFilm.getId());
        return updatedFilm;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
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

    public void likeFilm(Integer filmId, Integer userId) {
        userService.checkUserExists(userId);
        filmStorage.likeFilm(filmId, userId);
        log.info("Пользователь {} лайкнул фильм {}", userId, filmId);
    }

    public void removeLikeFilm(int filmId, int userId) {
        userService.checkUserExists(userId);
        filmStorage.removeLikeFilm(filmId, userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }

    public Collection<Film> topLikeFilm(int count) {
        return filmStorage.topLikeFilm(count);
    }
}

