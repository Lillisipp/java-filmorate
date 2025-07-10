package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
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

    public User updateUser(User updateUser) {
        if (updateUser.getId() == null) {
            log.warn("Обновление отклонено: ID не указан");
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
        if (userStorage.exist(updateUser)) {
            log.warn("Обновление отклонено: пользователь с ID {} не найден", updateUser.getId());
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }
        userStorage.update(updateUser);

        log.info("Пользователь с ID {} успешно обновлён", updateUser.getId());
        return updateUser;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public Optional<User> getUserById(Integer id) {
        return userStorage.getUserById(id);
    }


    public User addFriend(Integer id, Integer friendId) {
        userStorage.checkUserExists(id);
        userStorage.checkUserExists(friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", id, friendId);
        return userStorage.addFriend(id, friendId);
    }

    public User removeFriend(Integer id, Integer friendId) {
        userStorage.checkUserExists(id);
        userStorage.checkUserExists(friendId);

        return userStorage.removeFrend(id, friendId);
    }

    public Collection<User> getListFriends(Integer id) {
        userStorage.checkUserExists(id);
        return userStorage.getListFrends(id);
    }

    public Collection<User> getMutualFriends(Integer id, Integer friendId) {
        userStorage.checkUserExists(id);
        userStorage.checkUserExists(friendId);
        return userStorage.getMutualFrends(id, friendId);
    }


}
