package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserDbStorage userDbStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDbStorage = mock(UserDbStorage.class);
        userService = new UserService(userDbStorage);
    }

    @Test
    @DisplayName("Должен использоваться логин вместо имени, если имя пустое")
    void shouldUseLoginAsNameIfNameEmpty() {
        UserDto userDto = new UserDto(
                null,
                "test@mail.com",
                "login",
                "",
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );

        when(userDbStorage.save(any(User.class))).thenAnswer(invocation -> {
            final User user = invocation.getArgument(0);
            user.setId(1);
            return user;
        });

        UserDto created = userService.createUser(userDto);

        assertEquals("login", created.getName());
        assertEquals(1, created.getId());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDbStorage).save(captor.capture());
        User savedArg = captor.getValue();
        assertEquals("login", savedArg.getName());
        assertEquals("login", savedArg.getLogin());
        assertEquals("test@mail.com", savedArg.getEmail());
        assertEquals(LocalDate.of(2000, 1, 1), savedArg.getBirthday());
    }

    @Test
    void shouldNotUpdateUserWhenIdIsNull() {
        UserDto user = new UserDto(
                null,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>());

        ConditionsNotMetException ex = assertThrows(
                ConditionsNotMetException.class,
                () -> userService.updateUser(user)
        );

        assertEquals("Id должен быть указан.", ex.getMessage());

        verify(userDbStorage, never()).update(any());
    }

    @Test
    void shouldNotUpdateUserIfUserNotExist() {
        UserDto user = new UserDto(1,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );

        when(userDbStorage.exist(user.getId())).thenReturn(false);

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
        when(userDbStorage.exist(user.getId())).thenReturn(true);

        when(userDbStorage.update(user)).thenReturn(user);
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

        when(userDbStorage.addFriend(1, 2)).thenReturn(user);
        when(userDbStorage.exist(1)).thenReturn(true);
        when(userDbStorage.exist(2)).thenReturn(true);

        userService.addFriend(1, 2);

        verify(userDbStorage).exist(1);
        verify(userDbStorage).exist(2);
        verify(userDbStorage).addFriend(1, 2);
    }

    //    @Disabled
    @Test
    void removeFriend_success() {
        when(userDbStorage.exist(1)).thenReturn(true);
        when(userDbStorage.exist(2)).thenReturn(true);

        doNothing().when(userDbStorage).removeFriend(1, 2);

        userService.removeFriend(1, 2);

        verify(userDbStorage).exist(1);
        verify(userDbStorage).exist(2);
        verify(userDbStorage).removeFriend(1, 2);
        verifyNoMoreInteractions(userDbStorage);
    }


    @Test
    void testGetListFriends() {
        when(userDbStorage.exist(5)).thenReturn(true);
        when(userDbStorage.getListFriends(5)).thenReturn(List.of(new User()));

        userService.getListFriends(5);

        verify(userDbStorage).exist(5);
        verify(userDbStorage).getListFriends(5);
    }

    @Test
    void testGetMutualFriends() {
        when(userDbStorage.exist(1)).thenReturn(true);
        when(userDbStorage.exist(2)).thenReturn(true);
        when(userDbStorage.getMutualFriends(1, 2)).thenReturn(List.of(new User()));

        userService.getMutualFriends(1, 2);

        verify(userDbStorage).exist(1);
        verify(userDbStorage).exist(2);
        verify(userDbStorage).getMutualFriends(1, 2);
    }

    @Test
    void testGetUserById() {
        User user = new User(
                1,
                "test@mail.com",
                "login",
                "name",
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );

        when(userDbStorage.getUserById(1)).thenReturn(Optional.of(user));

        UserDto userById = userService.getUserById(1);

        assertAll(
                () -> assertEquals(1, userById.getId()),
                () -> assertEquals("test@mail.com", userById.getEmail()),
                () -> assertEquals("login", userById.getLogin()),
                () -> assertEquals("name", userById.getName()),
                () -> assertEquals(LocalDate.of(2000, 1, 1), userById.getBirthday()),
                () -> assertTrue(userById.getFriends().isEmpty())
        );

        verify(userDbStorage, times(1)).getUserById(1);
    }
}

