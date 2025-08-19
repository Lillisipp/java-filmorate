package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public FilmDto addFilm(@Valid @RequestBody FilmDto film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @GetMapping
    public Collection<FilmDto> getFilms() {
        return filmService.getFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(
            @PathVariable("id") int filmId,
            @PathVariable int userId
    ) {
        filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFilm(
            @PathVariable("id") int filmId,
            @PathVariable int userId
    ) {
        filmService.removeLikeFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> topLikeFilm(
            @RequestParam(defaultValue = "10") int count
    ) {
        return filmService.topLikeFilm(count);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        return filmService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<MpaRating> getMPA() {
        return filmService.getMpa();
    }

    @GetMapping("/mpa/{id}")
    public MpaRating getMPAById(@PathVariable Integer id) {
        return filmService.getMPAById(id);
    }
}
