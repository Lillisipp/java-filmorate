package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDbStorage userDbStorage;

    public void checkUserExists(Integer id) {
        if (!userDbStorage.exist(id)) {
            log.warn("Обновление отклонено: пользователь с ID {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    public UserDto createUser(User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        User save = userDbStorage.save(user);
        log.info("Пользователь создан с ID: {}", user.getId());
        return UserMapper.mapToUserDto(save);
    }

    public UserDto updateUser(User updateUser) {
        checkUserExists(updateUser.getId());
        User user = userDbStorage.update(updateUser);

        log.info("Пользователь с ID {} успешно обновлён", updateUser.getId());
        return UserMapper.mapToUserDto(user);
    }

    public Collection<UserDto> getUsers() {
        return userDbStorage
                .getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getUserById(Integer id) {
        User user = userDbStorage
                .getUserById(id)         // Optional<User>
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    public void deleteUser(Integer id) {
        checkUserExists(id);
        userDbStorage.delete(userDbStorage.getUserById(id).orElseThrow());
        log.info("Пользователь с ID {} удалён", id);
    }

    public UserDto addFriend(Integer id, Integer friendId) {
        checkUserExists(id);
        checkUserExists(friendId);
        log.info("Пользователь {} отправил запрос в друзья пользователю {}", id, friendId);
        User us =userDbStorage.addFriend(id, friendId);
        return UserMapper.mapToUserDto(us);
    }

    public Collection<UserDto> getListFriends(Integer id) {
        checkUserExists(id);
        return userDbStorage.getListFriends(id)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> getMutualFriends(Integer id, Integer friendId) {
        checkUserExists(id);
        checkUserExists(friendId);
        return userDbStorage.getMutualFriends(id, friendId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public void removeFriend(Integer id, Integer friendId) {
        checkUserExists(id);
        checkUserExists(friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", id, friendId);
        userDbStorage.removeFriend(id, friendId);
    }

    public UserDto confirmFriendRequest(Integer id, Integer friendId) {
        checkUserExists(id);
        checkUserExists(friendId);
        log.info("Пользователь {} подтвердил запрос в друзья от пользователя {}", id, friendId);
        User user=userDbStorage.confirmFriendRequest(id, friendId);
        return UserMapper.mapToUserDto(user);
    }
}
