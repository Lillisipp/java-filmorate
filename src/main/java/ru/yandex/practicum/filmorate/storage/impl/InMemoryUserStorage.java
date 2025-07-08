package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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

    @Override
    public User update(User updateUser) {
        users.put(updateUser.getId(), updateUser);
        return updateUser;
    }

    @Override
    public User delete(User user) {
        return null;
    }

    @Override
    public boolean exist(User user) {
        return users.containsKey(user.getId());
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }
}
