package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getUsers();
    User save(User user);
    User update(User newUser);
    User delete(User user);

    boolean exist(User user);

    Optional<User> getUserById(Integer id);
}
