package ru.yandex.practicum.filmorate.storege;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class UserStorageTest {
    @InjectMocks
    private InMemoryUserStorage userStorage;

    @Test
    void shouldSaveUserAndAssignId() {
        User user = new User(null,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );
        userStorage.save(user);
        assertNotNull(user.getId());
        assertEquals(1, userStorage.getUsers().size());
    }

    @Test
    void shouldUpdateUser() {
        User user = new User(null,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );
        userStorage.save(user);
        user.setName("Updated");
        userStorage.update(user);
        User updateUs = userStorage.getUserById(user.getId()).orElseThrow();
        assertEquals("Updated", user.getName());
    }

    @Test
    void shouldGetUserById() {
        User user = new User(null, "email@example.com", "login", "name", LocalDate.of(1990, 1, 1), Set.of());
        userStorage.save(user);

        var foundUser = userStorage.getUserById(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
    }


    @Test
    void shouldAddAndRemoveFriend() {
        User user1 = new User(
                null,
                "user1@example.com",
                "login1",
                "User1",
                LocalDate.of(1990, 1, 1),
                new HashSet<>());
        User user2 = new User(
                null,
                "user2@example.com",
                "login2",
                "User2",
                LocalDate.of(1991, 1, 1),
                new HashSet<>());

        userStorage.save(user1);
        userStorage.save(user2);

        userStorage.addFriend(user1.getId(), user2.getId());

        assertTrue(userStorage.getUserById(user1.getId()).get().getFriends().contains(user2.getId()));
        assertTrue(userStorage.getUserById(user2.getId()).get().getFriends().contains(user1.getId()));

        userStorage.removeFriend(user1.getId(), user2.getId());

        assertFalse(userStorage.getUserById(user1.getId()).get().getFriends().contains(user2.getId()));
    }

    @Test
    void shouldReturnMutualFriends() {
        User user1 = new User(null, "user1@example.com", "login1", "User1", LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(null, "user2@example.com", "login2", "User2", LocalDate.of(1991, 1, 1), new HashSet<>());
        User mutual = new User(null, "mutual@example.com", "mutual", "Mutual", LocalDate.of(1992, 1, 1), new HashSet<>());

        userStorage.save(user1);
        userStorage.save(user2);
        userStorage.save(mutual);

        userStorage.addFriend(user1.getId(), mutual.getId());
        userStorage.addFriend(user2.getId(), mutual.getId());

        var mutualFriends = userStorage.getMutualFriends(user1.getId(), user2.getId());

        assertEquals(1, mutualFriends.size());
        assertEquals(mutual.getId(), mutualFriends.iterator().next().getId());
    }

}
