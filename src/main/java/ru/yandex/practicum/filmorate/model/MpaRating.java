package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.MPA;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MpaRating {
    private Integer id;

    @JsonProperty("name")
    private MPA mpa;

}
