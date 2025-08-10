package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.GenreType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Genre {
    private Integer id;
    private GenreType type;
}
