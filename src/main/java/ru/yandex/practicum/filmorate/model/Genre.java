package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.GenreType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class Genre {
    private Integer id;

    @JsonProperty("name")
    private GenreType type;

}
