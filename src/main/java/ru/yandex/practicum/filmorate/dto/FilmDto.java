package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmDto {
    private Integer id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200)
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "MINUTES")
    private Duration duration;

    private Set<Genre> genres = new HashSet<>();
    private Set<Integer> likes = new HashSet<>();

    @NotNull
    private MpaRating mpa;
}
