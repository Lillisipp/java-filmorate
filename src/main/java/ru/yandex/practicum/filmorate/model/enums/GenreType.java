package ru.yandex.practicum.filmorate.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.utils.Utils.normalize;

@AllArgsConstructor
@Getter
public enum GenreType {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик"),
    HORROR("Ужасы"),
    FANTASTIC("Фантастика"),
    ROMANCE("Мелодрама");

    private static final Map<String, GenreType> GENRE_TYPE_MAP =
            Arrays
                    .stream(values())
                    .collect(
                            Collectors.toMap(
                                    g -> normalize(g.name),
                                    Function.identity()
                            )
                    );

    private final String name;

    public static GenreType fromName(String name) {
        GenreType gt = GENRE_TYPE_MAP.get(normalize(name));
        if (gt == null) {
            throw new IllegalArgumentException("Unknown genre: " + name);
        }
        return gt;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GenreType fromJson(String value) {
        return fromName(value);
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
