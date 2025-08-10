package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(
            @PathVariable Integer id,
            @PathVariable Integer friendId
    ) {
        return userService.addFriend(id, friendId);
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public UserDto confirmFriend(
            @PathVariable Integer id,
            @PathVariable Integer friendId
    ) {
        return userService.confirmFriendRequest(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable Integer id,
            @PathVariable Integer friendId
    ) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getListFriends(
            @PathVariable Integer id
    ) {
        return userService.getListFriends(id);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public Collection<UserDto> getMutualFriends(
            @PathVariable Integer id,
            @PathVariable Integer friendId
    ) {
        return userService.getMutualFriends(id, friendId);
    }
}
