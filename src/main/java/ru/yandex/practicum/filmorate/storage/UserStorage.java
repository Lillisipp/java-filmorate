package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    void checkUserExists(Integer id);

    boolean exist(User user);

    Collection<User> getUsers();

    User save(User user);

    User update(User newUser);

    User delete(User user);


    Optional<User> getUserById(Integer id);

    User addFriend(Integer id, Integer friendId);

    User removeFrend(Integer id, Integer friendId);

    Collection<User> getListFrends(Integer id);

    Collection<User> getMutualFrends(Integer id, Integer friendId);

}
