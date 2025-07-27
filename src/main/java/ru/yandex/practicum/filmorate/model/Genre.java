package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Genre {
    private Integer id;
    private ru.yandex.practicum.filmorate.model.enums.Genre genre;
}
