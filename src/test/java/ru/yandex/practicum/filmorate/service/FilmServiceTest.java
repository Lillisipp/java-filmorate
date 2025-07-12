package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class FilmServiceTest {
    @Mock
    private FilmStorage filmStorage;
    @Mock
    private UserService userService;

    @InjectMocks
    private FilmService filmService;

    @Test
    void addFilmSuccess() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDuration(Duration.ofMinutes(100));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        when(filmStorage.save(film)).thenReturn(film);

        Film result = filmService.addFilm(film);

        assertEquals(film, result);
        verify(filmStorage).save(film);
    }

    @Test
    void addFilmValidationFailDuration() {
        Film film = new Film();
        film.setDuration(Duration.ofMinutes(-10));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.addFilm(film));
        assertEquals("Продолжительность фильма должна быть положительным числом.", ex.getMessage());
    }

    @Test
    void addFilmValidationFailDate() {
        Film film = new Film();
        film.setDuration(Duration.ofMinutes(100));
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.addFilm(film));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года.", ex.getMessage());
    }

    @Test
    void updateFilmSuccess() {
        Film film = new Film();
        film.setId(1);
        film.setName("Updated Film");
        film.setDuration(Duration.ofMinutes(120));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        when(filmStorage.exist(film)).thenReturn(false);
        when(filmStorage.update(film)).thenReturn(film);

        Film result = filmService.updateFilm(film);

        assertEquals(film, result);
        verify(filmStorage).update(film);
    }

    @Test
    void updateFilmWithoutId() {
        Film film = new Film(); // ID = null

        ConditionsNotMetException ex = assertThrows(ConditionsNotMetException.class, () -> filmService.updateFilm(film));
        assertEquals("Id должен быть указан.", ex.getMessage());
    }

    @Test
    void updateFilmNotExists() {
        Film film = new Film();
        film.setId(1);
        film.setDuration(Duration.ofMinutes(100));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        when(filmStorage.exist(film)).thenReturn(true);

        ConditionsNotMetException ex = assertThrows(ConditionsNotMetException.class, () -> filmService.updateFilm(film));
        assertEquals("Фильм с таким ID не найден.", ex.getMessage());
    }

    @Test
    void getFilmsReturnsList() {
        List<Film> films = List.of(new Film(), new Film());
        when(filmStorage.getFilms()).thenReturn(films);

        assertEquals(films, filmService.getFilms());
    }

    @Test
    void likeFilmSuccess() {
        int filmId = 1;
        int userId = 42;

        doNothing().when(userService).checkUserExists(userId);
        doNothing().when(filmStorage).likeFilm(filmId, userId);

        filmService.likeFilm(filmId, userId);

        verify(userService).checkUserExists(userId);
        verify(filmStorage).likeFilm(filmId, userId);
    }

    @Test
    void removeLikeFilmSuccess() {
        int filmId = 1;
        int userId = 42;

        doNothing().when(userService).checkUserExists(userId);
        doNothing().when(filmStorage).removeLikeFilm(filmId, userId);

        filmService.removeLikeFilm(filmId, userId);

        verify(userService).checkUserExists(userId);
        verify(filmStorage).removeLikeFilm(filmId, userId);
    }

    @Test
    void topLikeFilmReturnsTop() {
        List<Film> topFilms = List.of(new Film(), new Film());
        when(filmStorage.topLikeFilm(2)).thenReturn(topFilms);

        assertEquals(topFilms, filmService.topLikeFilm(2));
        verify(filmStorage).topLikeFilm(2);
    }
}

