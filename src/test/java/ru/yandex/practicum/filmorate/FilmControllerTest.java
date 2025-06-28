package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddFilmSuccess() throws Exception {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A sci-fi movie about dreams.");
        film.setDuration(Duration.ofMinutes(148));
        film.setReleaseDate(LocalDate.of(2010, 7, 16));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Inception")))
                .andExpect(jsonPath("$.description", is("A sci-fi movie about dreams.")));
    }

    @Test
    void testAddFilmValidationErrorEmptyName() throws Exception {
        Film film = new Film(1, " ", "abbb", LocalDate.of(2010, 7, 16), Duration.ofMinutes(148));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
//                .andDo(print());
                .andExpect(jsonPath("$.name", containsString("Название не может быть пустым")));
    }

    @Test
    void testUpdateFilmSuccess() throws Exception {
        Film film = new Film();
        film.setName("Interstellar");
        film.setDescription("A sci-fi movie about space.");
        film.setDuration(Duration.ofMinutes(169));
        film.setReleaseDate(LocalDate.of(2014, 11, 7));

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
        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Non-existent Film");
        updatedFilm.setDescription("This film does not exist.");
        updatedFilm.setDuration(Duration.ofMinutes(120));
        updatedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", containsString("Фильм с таким ID не найден")));
    }

    @Test
    void testGetFilm() throws Exception {
        // Создаём два фильма
        Film film1 = new Film(1, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), Duration.ofMinutes(120));
        Film film2 = new Film(2, "Film 2", "Description 2", LocalDate.of(2010, 5, 10), Duration.ofMinutes(90));

        // Добавляем фильмы в коллекцию
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().isOk());

        // Проверяем получение всех фильмов
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


}
