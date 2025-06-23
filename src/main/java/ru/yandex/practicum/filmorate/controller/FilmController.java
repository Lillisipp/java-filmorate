package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/film")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@RequestBody Film film) {


        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
Film oldFilm=films
        return;
    }

    @GetMapping
    public Collection<Film> getFilm() {
        return films.values();
    }

}
