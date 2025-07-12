package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

        checkUserExists(updateUser.getId());
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
        checkUserExists(id);
        checkUserExists(friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", id, friendId);
        return userStorage.addFriend(id, friendId);
    }

    public void removeFriend(Integer id, Integer friendId) {
        userStorage.removeFriend(id, friendId);
    }

    public Collection<User> getListFriends(Integer id) {
        checkUserExists(id);
        return userStorage.getListFriends(id);
    }

    public Collection<User> getMutualFriends(Integer id, Integer friendId) {
        checkUserExists(id);
        checkUserExists(friendId);
        return userStorage.getMutualFriends(id, friendId);
    }

    public void checkUserExists(Integer id) {
        if (!userStorage.exist(id)) {
            log.warn("Обновление отклонено: пользователь с ID {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }
}
