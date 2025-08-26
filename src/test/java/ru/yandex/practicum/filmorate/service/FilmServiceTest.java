package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        FilmDto dto = new FilmDto();
        dto.setName("Test Film");
        dto.setDuration(Duration.ofMinutes(100));
        dto.setReleaseDate(LocalDate.of(2000, 1, 1));

        when(filmStorage.save(any(Film.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        FilmDto result = filmService.addFilm(dto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("Test Film", result.getName()),
                () -> assertEquals(LocalDate.of(2000, 1, 1), result.getReleaseDate()),
                () -> assertEquals(Duration.ofMinutes(100), result.getDuration())
        );
        verify(filmStorage).save(any(Film.class));
    }

    @Test
    void addFilmValidationFailDuration() {
        FilmDto film = new FilmDto();
        film.setDuration(Duration.ofMinutes(-10));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.addFilm(film));
        assertEquals("Продолжительность фильма должна быть положительным числом.", ex.getMessage());
    }

    @Test
    void addFilmValidationFailDate() {
        FilmDto film = new FilmDto();
        film.setDuration(Duration.ofMinutes(100));
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.addFilm(film));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года.", ex.getMessage());
    }

    @Test
    void updateFilmSuccess() {
        // входной DTO
        FilmDto dto = new FilmDto();
        dto.setId(1);
        dto.setName("Updated Film");
        dto.setDuration(Duration.ofMinutes(120));
        dto.setReleaseDate(LocalDate.of(2000, 1, 1));

        // entity после mapToFilm
        Film mapped = new Film();
        mapped.setId(1);
        mapped.setName("Updated Film");
        mapped.setDuration(Duration.ofMinutes(120));
        mapped.setReleaseDate(LocalDate.of(2000, 1, 1));

        // entity, возвращаемая storage.update
        Film updated = new Film();
        updated.setId(1);
        updated.setName("Updated Film");
        updated.setDuration(Duration.ofMinutes(120));
        updated.setReleaseDate(LocalDate.of(2000, 1, 1));

        // ожидаемый DTO после mapToFilmDto
        FilmDto expected = new FilmDto();
        expected.setId(1);
        expected.setName("Updated Film");
        expected.setDuration(Duration.ofMinutes(120));
        expected.setReleaseDate(LocalDate.of(2000, 1, 1));

        try (MockedStatic<FilmMapper> mapper = Mockito.mockStatic(FilmMapper.class)) {
            mapper.when(() -> FilmMapper.mapToFilm(dto)).thenReturn(mapped);
            mapper.when(() -> FilmMapper.mapToFilmDto(updated)).thenReturn(expected);

            when(filmStorage.getFilmById(1)).thenReturn(Optional.of(new Film()));

            when(filmStorage.update(mapped)).thenReturn(updated);

            FilmDto result = filmService.updateFilm(dto);

            assertEquals(expected, result);
            verify(filmStorage).getFilmById(1);
            verify(filmStorage).update(mapped);
            verifyNoMoreInteractions(filmStorage);
        }
    }

    @Test
    void updateFilmWithoutId() {
        FilmDto film = new FilmDto(); // ID = null

        ConditionsNotMetException ex = assertThrows(ConditionsNotMetException.class, () -> filmService.updateFilm(film));
        assertEquals("Id должен быть указан.", ex.getMessage());
    }

    @Test
    void updateFilmNotExists() {
        FilmDto film = new FilmDto();
        film.setId(1);
        film.setDuration(Duration.ofMinutes(100));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));


        when(filmStorage.getFilmById(1)).thenReturn(Optional.empty());

        ConditionsNotMetException ex = assertThrows(ConditionsNotMetException.class, () -> filmService.updateFilm(film));
        assertEquals("Фильм с таким ID не найден.", ex.getMessage());
    }

    @Test
    void getFilmsReturnsList() {
        Film f1 = new Film();
        f1.setId(1);
        f1.setName("A");

        Film f2 = new Film();
        f2.setId(2);
        f2.setName("B");

        when(filmStorage.getFilms()).thenReturn(List.of(f1, f2));

        Collection<FilmDto> result = filmService.getFilms();

        List<FilmDto> expected = new ArrayList<>();
        expected.add(FilmMapper.mapToFilmDto(f1));
        expected.add(FilmMapper.mapToFilmDto(f2));

        assertIterableEquals(expected, result);
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
        Film f1 = new Film();
        f1.setId(1);
        f1.setName("A");
        Film f2 = new Film();
        f2.setId(2);
        f2.setName("B");
        when(filmStorage.topLikeFilm(2)).thenReturn(List.of(f1, f2)); // <-- List<Film>

        // вызываем сервис
        Collection<FilmDto> result = filmService.topLikeFilm(2);        // <-- Collection<FilmDto>

        // ожидаемое (построим через реальный статический маппер)
        List<FilmDto> expected = List.of(
                FilmMapper.mapToFilmDto(f1),
                FilmMapper.mapToFilmDto(f2)
        );

        assertIterableEquals(expected, result);
        verify(filmStorage).topLikeFilm(2);
    }
}

