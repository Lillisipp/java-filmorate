package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);
        user.setId(Utils.getNextId(users)); // присваиваем ID
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
        if (!users.containsKey(newUser.getId())) {
            throw new ConditionsNotMetException("Пользователь с таким ID не найден.");
        }

        validateUser(newUser);

        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    private void validateUser(User user) {
        if (!StringUtils.hasText(user.getEmail()) || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'.");
        }
        if (!StringUtils.hasText(user.getLogin()) || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}
