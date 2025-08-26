package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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

    public UserDto createUser(UserDto dto) {
        log.info("Получен запрос на создание пользователя: {}", dto);
        if (!StringUtils.hasText(dto.getName())) {
            dto.setName(dto.getLogin());
        }
        User user = UserMapper.mapToUser(dto);
        User saved = userDbStorage.save(user);
        log.info("Пользователь создан с ID: {}", saved.getId());
        return UserMapper.mapToUserDto(saved);
    }

    public UserDto updateUser(UserDto updateUser) {
        if (updateUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
        checkUserExists(updateUser.getId());

        User user = UserMapper.mapToUser(updateUser);
        User updated = userDbStorage.update(user);

        log.info("Пользователь с ID {} успешно обновлён", updateUser.getId());
        return UserMapper.mapToUserDto(updated);
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
                .getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    public void deleteUser(Integer id) {
        checkUserExists(id);
        userDbStorage.delete(userDbStorage.getUserById(id).orElseThrow());
        log.info("Пользователь с ID {} удалён", id);
    }

    public UserDto addFriend(Integer id, Integer friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("Нельзя добавить себя в друзья.");
        }
        checkUserExists(id);
        checkUserExists(friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", id, friendId);
        User us = userDbStorage.addFriend(id, friendId);
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

}
