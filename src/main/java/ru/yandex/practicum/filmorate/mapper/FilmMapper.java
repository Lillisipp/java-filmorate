package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;

@UtilityClass
public class FilmMapper {

    public FilmDto mapToFilmDto(Film film) {
        return new FilmDto()
                .setId(film.getId())
                .setName(film.getName())
                .setDescription(film.getDescription())
                .setReleaseDate(film.getReleaseDate())
                .setGenres(new HashSet<>(film.getGenres()))
                .setDuration(film.getDuration())
                .setLikes(new HashSet<>(film.getLikes()))
                .setMpa(film.getMpa());
    }

    public Film mapToFilm(FilmDto dto) {
        return new Film()
                .setId(dto.getId())
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setReleaseDate(dto.getReleaseDate())
                .setGenres(new HashSet<>(dto.getGenres()))
                .setDuration(dto.getDuration())
                .setLikes(new HashSet<>(dto.getLikes()))
                .setMpa(dto.getMpa());
    }
}
