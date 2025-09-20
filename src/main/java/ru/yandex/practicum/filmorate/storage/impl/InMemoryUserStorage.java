package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;


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
        users.remove(user.getId());
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
        User friend = users.get(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        user.getFriends().add(new Friendship(friendId, FriendshipStatus.UNCONFIRMED));
        return user;
    }

    @Override
    public void removeFriend(Integer id, Integer friendId) {
        User user = getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
        User friend = getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));

        user.getFriends().removeIf(f -> f.getFriendId().equals(friendId));
        friend.getFriends().removeIf(f -> f.getFriendId().equals(id));
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
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.UNCONFIRMED)
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> getMutualFriends(Integer id, Integer otherId) {
        User user = users.get(id);
        User other = users.get(otherId);
        if (user == null || other == null) {
            return Collections.emptyList();
        }
        Set<Integer> confirmedFriends = user.getFriends().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        return other.getFriends().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED && confirmedFriends.contains(f.getFriendId()))
                .map(f -> users.get(f.getFriendId()))
                .collect(Collectors.toList());
    }

    @Override
    public User confirmFriendRequest(Integer id, Integer friendId) {
        User user = users.get(id);
        User friend = users.get(friendId);
        if (friend == null || user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        friend.getFriends().stream()
                .filter(f -> f.getFriendId().equals(id) && f.getStatus() == FriendshipStatus.UNCONFIRMED)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Запрос в друзья не найден"))
                .setStatus(FriendshipStatus.CONFIRMED);
        user.getFriends().add(new Friendship(friendId, FriendshipStatus.CONFIRMED));
        return user;
    }
}
