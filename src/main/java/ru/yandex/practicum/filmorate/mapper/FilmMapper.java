package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;

public class FilmMapper {

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto fDto = new FilmDto();
        fDto.setId(film.getId());
        fDto.setName(film.getName());
        fDto.setDescription(film.getDescription());
        fDto.setReleaseDate(film.getReleaseDate());
        fDto.setDuration(film.getDuration());
        fDto.setGenres(new HashSet<>(film.getGenres()));
        fDto.setLikes(new HashSet<>(film.getLikes()));
        fDto.setMpa(film.getMpa());
        return fDto;
    }
}
