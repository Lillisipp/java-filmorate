package ru.yandex.practicum.filmorate.utils;

import java.util.Map;

public class Utils {
    public static int getNextId(Map<Integer, ?> map) {
        return map.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
