package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        userStorage.save(user);
        log.info("Пользователь создан с ID: {}", user.getId());
        return user;
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Обновление отклонено: ID не указан");
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
        if (!users.containsKey(newUser.getId())) {
            log.warn("Обновление отклонено: пользователь с ID {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }
userStorage.update(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с ID {} успешно обновлён", newUser.getId());
        return newUser;
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public Optional<User> getUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }


    public User addFriend(Integer id, Integer friendId) {
    }
}
