package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.util.*;


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
    public void delete(User user) {
        ;
    }

    @Override
    public boolean exist(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User addFriend(Integer id, Integer friendId) {
        User user = users.get(id);
        User userFrend = users.get(friendId);
        if (user != null && friendId != null) {
            user.getFriends().add(friendId);
            userFrend.getFriends().add(id);
        }
        return user;
    }

    @Override
    public void removeFriend(Integer id, Integer friendId) {
        getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"))
                .getFriends()
                .remove(friendId);
    }

    @Override
    public Collection<User> getListFriends(Integer id) {
        User user = users.get(id);
        if (user == null) {
            return Collections.emptyList();
        }
        return user
                .getFriends()
                .stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> getMutualFriends(Integer id, Integer otherdId) {
        User user = users.get(id);
        User other = users.get(otherdId);
        if (user == null || other == null) {
            return Collections.emptyList();
        }
        Set<Integer> mutualIds = new HashSet<>(user.getFriends());
        mutualIds.retainAll(other.getFriends());
        return mutualIds.stream()
                .map(users::get)
                .toList();
    }
}
