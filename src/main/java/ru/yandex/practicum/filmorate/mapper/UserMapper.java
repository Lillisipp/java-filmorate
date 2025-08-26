package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;

@UtilityClass
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setLogin(user.getLogin())
                .setName(user.getName())
                .setBirthday(user.getBirthday())
                .setFriends(new HashSet<>(user.getFriends()));
    }

    public static User mapToUser(UserDto dto) {
        return new User()
                .setId(dto.getId())
                .setEmail(dto.getEmail())
                .setLogin(dto.getLogin())
                .setName(dto.getName())
                .setBirthday(dto.getBirthday())
                .setFriends(new HashSet<>(dto.getFriends()));
    }
}
