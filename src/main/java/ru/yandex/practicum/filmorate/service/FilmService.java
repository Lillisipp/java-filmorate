package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmDbStorage;
    private final UserService userService;

    public FilmDto addFilm(FilmDto dto) {
        log.debug("Создание нового фильма: {}", dto);
        validateFilm(dto);
        Film film = FilmMapper.mapToFilm(dto);
        filmDbStorage.save(film);
        log.debug("Фильм добавлен с ID: {}", dto.getId());
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(FilmDto updatedFilm) {
        if (updatedFilm.getId() == null) {
            log.warn("Обновление отменено — ID не указан.");
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
        validateFilm(updatedFilm);
        Film updated = FilmMapper.mapToFilm(updatedFilm);
        if (!filmDbStorage.exist(updated)) {
            log.warn("Обновление отменено — фильм с ID {} не найден.", updatedFilm.getId());
            throw new ConditionsNotMetException("Фильм с таким ID не найден.");
        }
        filmDbStorage.update(updated);
        log.debug("Фильм с ID {} успешно обновлён.", updatedFilm.getId());
        return FilmMapper.mapToFilmDto(updated);
    }

    public Collection<FilmDto> getFilms() {
        return filmDbStorage
                .getFilms()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private void validateFilm(FilmDto film) {
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
        filmDbStorage.likeFilm(filmId, userId);
        log.info("Пользователь {} лайкнул фильм {}", userId, filmId);
    }

    public void removeLikeFilm(int filmId, int userId) {
        userService.checkUserExists(userId);
        filmDbStorage.removeLikeFilm(filmId, userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }

    public Collection<FilmDto> topLikeFilm(int count) {
        if (count <= 0) {
            throw new ValidationException("count должен быть положительным числом.");
        }
        return filmDbStorage
                .topLikeFilm(count)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto getFilmById(Integer id) {
        return filmDbStorage
                .getFilmById(id)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден."));
    }

    public List<Genre> getGenres() {
        return filmDbStorage.getGeners();
    }

    public Genre getGenreById(Integer id) {
        return filmDbStorage.getGenerById(id)
                .orElseThrow(() -> new NotFoundException("Жанр id=" + id + " не найден"));
    }

    public List<MpaRating> getMpa() {
        return filmDbStorage.getMpa();
    }

    public MpaRating getMPAById(Integer id) {
        return filmDbStorage.getMPAById(id)
                .orElseThrow(() -> new NotFoundException("MPA id=" + id + " не найден"));
    }
}

