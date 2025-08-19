package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.enums.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmControllerTest {
    private final MpaRating DEFAULT_MPA = new MpaRating(1, MPA.G);
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddFilmSuccess() throws Exception {
        FilmDto film = new FilmDto(
                null,
                "Inception",
                "A sci-fi movie about dreams.",
                LocalDate.of(2010, 7, 16),
                Duration.ofMinutes(148),
                Set.of(),
                Set.of(),
                DEFAULT_MPA
        );

        FilmDto resp = new FilmDto(
                1,
                "Inception",
                "A sci-fi movie about dreams.",
                LocalDate.of(2010, 7, 16),
                Duration.ofMinutes(148),
                Set.of(),
                new HashSet<>(),
                DEFAULT_MPA
        );

        when(filmService.addFilm(any(FilmDto.class))).thenReturn(resp);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Inception")))
                .andExpect(jsonPath("$.description", is("A sci-fi movie about dreams.")))
                .andExpect(jsonPath("$.releaseDate", is("2010-07-16")))
                .andExpect(jsonPath("$.duration", is(148)))
                .andDo(print());
    }

    @Test
    void testAddFilmValidationErrorEmptyName() throws Exception {
        FilmDto film = new FilmDto(
                1,
                " ",
                "abbb",
                LocalDate.of(2010, 7, 16),
                Duration.ofMinutes(148),
                Set.of(),
                Set.of(),
                DEFAULT_MPA
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.name", containsString("Название не может быть пустым")));
    }

    @Test
    void testUpdateFilmSuccess() throws Exception {
        FilmDto film = new FilmDto(
                null,
                "Interstellar",
                "A sci‑fi movie about space.",
                LocalDate.of(2014, 11, 7),
                Duration.ofMinutes(169),
                Set.of(),
                Set.of(),
                DEFAULT_MPA

        );

        when(filmService.updateFilm(film)).thenReturn(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andDo(print());

        film.setId(1);
        film.setName("Interstellar Updated");
        film.setDescription("Updated description.");
        film.setDuration(Duration.ofMinutes(180));
        film.setReleaseDate(LocalDate.of(2014, 11, 7));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Interstellar Updated")))
                .andExpect(jsonPath("$.description", is("Updated description.")));
    }


    @Test
    void testUpdateFilmNotFound() throws Exception {
        FilmDto updatedFilm = new FilmDto(
                1,
                "Non-existent Film",
                "This film does not exist.",
                LocalDate.of(2000, 1, 1),
                Duration.ofMinutes(120),
                Set.of(),
                Set.of(),
                DEFAULT_MPA
        );

        when(filmService.updateFilm(any(FilmDto.class)))
                .thenThrow(new NotFoundException("Фильм с таким ID не найден."));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", containsString("Фильм с таким ID не найден")));
    }

    @Test
    void testGetFilms() throws Exception {
        // Создаём два фильма
        FilmDto film1 = new FilmDto(
                1,
                "Film 1",
                "Description 1",
                LocalDate.of(2000, 1, 1),
                Duration.ofMinutes(120),
                Set.of(),
                Set.of(),
                DEFAULT_MPA
        );
        FilmDto film2 = new FilmDto(
                2,
                "Film 2",
                "Description 2",
                LocalDate.of(2010, 5, 10),
                Duration.ofMinutes(90),
                Set.of(),
                Set.of(),
                DEFAULT_MPA
        );
        when(filmService.getFilms()).thenReturn(List.of(film1, film2));

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Film 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[1].name").value("Film 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andDo(print());
    }

    @Test
    void testCreateFilmReleaseDate() throws Exception {
        FilmDto film = new FilmDto(
                null,
                "Первый фильм",
                "Исторический фильм, ровно на дату первого показа.",
                LocalDate.of(1895, 12, 28),
                Duration.ofMinutes(50),
                Set.of(),
                Set.of(),
                DEFAULT_MPA
        );

        when(filmService.addFilm(any(FilmDto.class))).thenReturn(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.releaseDate", is("1895-12-28")));
    }

    @Test
    void testCreateFilmDescriptionExactly200Characters() throws Exception {
        String description = "a".repeat(200); // Ровно 200 символов

        FilmDto film = new FilmDto(
                null,
                "Фильм с длинным описанием",
                description,
                LocalDate.of(2000, 1, 1),
                Duration.ofMinutes(120),
                Set.of(),
                Set.of(),
                DEFAULT_MPA
        );

        when(filmService.addFilm(film)).thenReturn(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(description)));
    }
}
