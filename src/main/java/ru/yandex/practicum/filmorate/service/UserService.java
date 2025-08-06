package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void checkUserExists(Integer id) {
        if (!userRepository.exist(id)) {
            log.warn("Обновление отклонено: пользователь с ID {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    public User createUser(User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        userRepository.save(user);
        log.info("Пользователь создан с ID: {}", user.getId());
        return user;
    }

    public User updateUser(User updateUser) {

        checkUserExists(updateUser.getId());
        userRepository.update(updateUser);

        log.info("Пользователь с ID {} успешно обновлён", updateUser.getId());
        return updateUser;
    }

    public Collection<User> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toCollection());
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.getUserById(id);
    }

    public User addFriend(Integer id, Integer friendId) {
        checkUserExists(id);
        checkUserExists(friendId);
        log.info("Пользователь {} отправил запрос в друзья пользователю {}", id, friendId);
        return userRepository.addFriend(id, friendId);
    }

    public Collection<User> getListFriends(Integer id) {
        checkUserExists(id);
        return userRepository.getListFriends(id);
    }

    public Collection<User> getMutualFriends(Integer id, Integer friendId) {
        checkUserExists(id);
        checkUserExists(friendId);
        return userRepository.getMutualFriends(id, friendId);
    }

    public void removeFriend(Integer id, Integer friendId) {
        checkUserExists(id);
        checkUserExists(friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", id, friendId);
        userRepository.removeFriend(id, friendId);
    }

    public User confirmFriendRequest(Integer id, Integer friendId) {
        checkUserExists(id);
        checkUserExists(friendId);
        log.info("Пользователь {} подтвердил запрос в друзья от пользователя {}", id, friendId);
        return userRepository.confirmFriendRequest(id, friendId);
    }
}
