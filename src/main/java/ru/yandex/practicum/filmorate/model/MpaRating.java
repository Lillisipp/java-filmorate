package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.MPA;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MpaRating {
    private Integer id;
    private MPA mpa;
}
