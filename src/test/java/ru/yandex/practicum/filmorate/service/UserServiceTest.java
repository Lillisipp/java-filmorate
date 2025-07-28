package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userStorage = mock(UserStorage.class);
        userService = new UserService(userStorage);
    }

    @Test
    @DisplayName("Должен использоваться логин вместо имени, если имя пустое")
    void shouldUseLoginAsNameIfNameEmpty() {
        User user = new User(
                null,
                "test@mail.com",
                "login",
                "",
                LocalDate.of(2000, 1, 1),
                new HashSet<>());
        when(userStorage.save(user)).thenReturn(user);

        User created = userService.createUser(user);

        assertEquals("login", created.getName());
        verify(userStorage).save(user);
    }

    @Test
    void shouldNotUpdateUserWhenIdIsNull() {
        User user = new User(
                null,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>());


        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(user)
        );

        assertEquals("Пользователь с id = null не найден", ex.getMessage());

        verify(userStorage, never()).update(any());
    }

    @Test
    void shouldNotUpdateUserIfUserNotExist() {
        User user = new User(1,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );

        when(userStorage.exist(user.getId())).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.updateUser(user));
        assertEquals("Пользователь с id = 1 не найден", ex.getMessage());
    }

    @Test
    void testUpdateUser() {
        User user = new User(1,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );

        when(userStorage.exist(user.getId())).thenReturn(true);

        when(userStorage.update(user)).thenReturn(user);
    }

    @Test
    void testAddFriend() {
        User user = new User(1,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );

        when(userStorage.addFriend(1, 2)).thenReturn(user);
        when(userStorage.exist(1)).thenReturn(true);
        when(userStorage.exist(2)).thenReturn(true);

        userService.addFriend(1, 2);

        verify(userStorage).exist(1);
        verify(userStorage).exist(2);
        verify(userStorage).addFriend(1, 2);
    }

//    @Test
//    void removeFriend_success() {
//        // Arrange: создаём двух «существующих» пользователей
//        User user1 = new User(
//                1,
//                "u1@mail.com",
//                "login1",
//                "User One",
//                LocalDate.of(1990, 1, 1),
//                new HashSet<>()
//        );
//        User user2 = new User(
//                2,
//                "u2@mail.com",
//                "login2",
//                "User Two",
//                LocalDate.of(1991, 2, 2),
//                new HashSet<>()
//        );
//
//        // Мокаем получение пользователей
//        when(userStorage.getUserById(1)).thenReturn(Optional.of(user1));
//        when(userStorage.getUserById(2)).thenReturn(Optional.of(user2));
//
//        // Мокаем удаление в хранилище
//        doNothing().when(userStorage).removeFriend(1, 2);
//
//        // Act
//        userService.removeFriend(1, 2);
//
//        // Assert: проверяем, что метод проверки существования вызван для обоих пользователей
//        verify(userStorage, times(1)).getUserById(1);
//        verify(userStorage, times(1)).getUserById(2);
//        // и что удаление было передано в хранилище
//        verify(userStorage, times(1)).removeFriend(1, 2);
//    }


    @Test
    void testGetListFriends() {
        when(userStorage.exist(5)).thenReturn(true);
        when(userStorage.getListFriends(5)).thenReturn(List.of(new User()));

        userService.getListFriends(5);

        verify(userStorage).exist(5);
        verify(userStorage).getListFriends(5);
    }

    @Test
    void testGetMutualFriends() {
        when(userStorage.exist(1)).thenReturn(true);
        when(userStorage.exist(2)).thenReturn(true);
        when(userStorage.getMutualFriends(1, 2)).thenReturn(List.of(new User()));

        userService.getMutualFriends(1, 2);

        verify(userStorage).exist(1);
        verify(userStorage).exist(2);
        verify(userStorage).getMutualFriends(1, 2);
    }

    @Test
    void testGetUserById() {
        User user = new User(1,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );
        when(userService.getUserById(1)).thenReturn(Optional.of(user));
        assertTrue(userService.getUserById(1).isPresent());
        verify(userStorage, times(1)).getUserById(1);

    }
}

