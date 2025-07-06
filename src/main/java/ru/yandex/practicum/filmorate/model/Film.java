package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Film {
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


}
