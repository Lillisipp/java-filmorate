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
public enum MPA {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private static final Map<String, MPA> MPA_TYPE_MAP =
            Arrays
                    .stream(values())
                    .collect(
                            Collectors.toMap(
                                    g -> normalize(g.name),
                                    Function.identity()
                            )
                    );
    private final String name;

    public static MPA fromName(String name) {
        MPA mpa = MPA_TYPE_MAP.get(normalize(name));
        if (mpa == null) {
            throw new IllegalArgumentException("Unknown genre: " + name);
        }
        return mpa;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MPA fromJson(String value) {
        return fromName(value);
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
