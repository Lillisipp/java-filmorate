package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();
    User save(User user);
    User update(User newUser);
    User delete(User user);

    boolean exist(User user);

    User getUserById(Integer id);
}
