package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User save(User user) {
        user.setId(Utils.getNextId(users)); // присваиваем ID
        return users.put(user.getId(), user);
    }
}
