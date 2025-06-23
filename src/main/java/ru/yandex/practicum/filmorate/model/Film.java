package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Film.
 */
@Getter
@Setter
@AllArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
}
