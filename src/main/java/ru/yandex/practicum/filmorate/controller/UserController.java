package ru.yandex.practicum.filmorate.controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (StringUtils.hasText(user.getEmail() && user.getEmail().contains("@")) ){
        return user;

        }
    }

    private final Map<Integer, User> users = new HashMap<>();

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        User oldUser=users.get(newUser.getId());
        return oldUser;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }


}
