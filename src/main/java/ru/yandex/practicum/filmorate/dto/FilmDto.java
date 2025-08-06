package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate releaseDate;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Duration duration;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Genre> genres = new HashSet<>();

    private Set<Integer> likes = new HashSet<>();

    private MpaRating mpa;
}
