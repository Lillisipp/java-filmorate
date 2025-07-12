package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getUsers();

    User save(User user);

    User update(User newUser);

    void delete(User user);

    boolean exist(Integer id);

    Optional<User> getUserById(Integer id);

    User addFriend(Integer id, Integer friendId);

    void removeFriend(Integer id, Integer friendId);

    Collection<User> getListFriends(Integer id);

    Collection<User> getMutualFriends(Integer id, Integer friendId);
}
