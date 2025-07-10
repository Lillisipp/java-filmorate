package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable int filmId, @PathVariable int userId) {
        return filmService.likeFilm(filmId,userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLikeFilm() {
        return filmService.DeletelikeFilm(likeFilm().getId());
    }

    @GetMapping("/popular?count={count}")
    public Collection<Film> TopLikeFilm() {
        return filmService.TopLikeFilm();
    }
    //возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10.


}
