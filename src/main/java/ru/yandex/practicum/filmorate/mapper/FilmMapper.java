package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;

//то что получает клиент
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto fDto = new FilmDto();
        fDto.setId(film.getId());
        fDto.setName(film.getName());
        fDto.setDescription(film.getDescription());
        fDto.setReleaseDate(film.getReleaseDate());
        fDto.setDuration(film.getDuration());
        fDto.setLikes(new HashSet<>(film.getLikes()));
        fDto.setGenres(new HashSet<>(film.getGenres()));
        fDto.setMpa(film.getMpa());
        return fDto;
    }
}
