package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        user.setId(Utils.getNextId(users)); // присваиваем ID
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь создан с ID: {}", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Обновление отклонено: ID не указан");
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
        if (!users.containsKey(newUser.getId())) {
            log.warn("Обновление отклонено: пользователь с ID {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }

        users.put(newUser.getId(), newUser);
        log.info("Пользователь с ID {} успешно обновлён", newUser.getId());
        return newUser;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }
}
