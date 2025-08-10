//package ru.yandex.practicum.filmorate.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.dto.UserDto;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.mapper.UserMapper;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;
//
//import java.time.LocalDate;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class UserServiceTest {
//    private UserDbStorage userDbStorage;
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        userDbStorage = mock(UserDbStorage.class);
//        userService = new UserService(userDbStorage);
//    }
//
//    @Test
//    @DisplayName("Должен использоваться логин вместо имени, если имя пустое")
//    void shouldUseLoginAsNameIfNameEmpty() {
//        User user = new User(
//                null,
//                "test@mail.com",
//                "login",
//                "",
//                LocalDate.of(2000, 1, 1),
//                new HashSet<>());
//        when(userDbStorage.save(user)).thenReturn(user);
//
//        UserDto created = userService.createUser(user);
//
//        assertEquals("login", created.getName());
//        verify(userDbStorage).save(user);
//    }
//
//    @Test
//    void shouldNotUpdateUserWhenIdIsNull() {
//        User user = new User(
//                null,
//                "test@mail.com",
//                "login",
//                "name",
//                LocalDate.of(2000, 1, 1),
//                new HashSet<>());
//
//
//        NotFoundException ex = assertThrows(
//                NotFoundException.class,
//                () -> userService.updateUser(user)
//        );
//
//        assertEquals("Пользователь с id = null не найден", ex.getMessage());
//
//        verify(userDbStorage, never()).update(any());
//    }
//
//    @Test
//    void shouldNotUpdateUserIfUserNotExist() {
//        User user = new User(1,
//                "test@mail.com",
//                "login",
//                "name",
//                LocalDate.of(2000, 1, 1),
//                new HashSet<>()
//        );
//
//        when(userDbStorage.exist(user.getId())).thenReturn(false);
//
//        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.updateUser(user));
//        assertEquals("Пользователь с id = 1 не найден", ex.getMessage());
//    }
//
//    @Test
//    void testUpdateUser() {
//        User user = new User(1,
//                "test@mail.com",
//                "login",
//                "name",
//                LocalDate.of(2000, 1, 1),
//                new HashSet<>()
//        );
//
//        when(userDbStorage.exist(user.getId())).thenReturn(true);
//
//        when(userDbStorage.update(user)).thenReturn(user);
//    }
//
//    @Test
//    void testAddFriend() {
//        User user = new User(1,
//                "test@mail.com",
//                "login",
//                "name",
//                LocalDate.of(2000, 1, 1),
//                new HashSet<>()
//        );
//
//        when(userDbStorage.addFriend(1, 2)).thenReturn(user);
//        when(userDbStorage.exist(1)).thenReturn(true);
//        when(userDbStorage.exist(2)).thenReturn(true);
//
//        userService.addFriend(1, 2);
//
//        verify(userDbStorage).exist(1);
//        verify(userDbStorage).exist(2);
//        verify(userDbStorage).addFriend(1, 2);
//    }
//
//    @Disabled
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
//        when(userDbStorage.getUserById(1)).thenReturn(Optional.of(user1));
//        when(userDbStorage.getUserById(2)).thenReturn(Optional.of(user2));
//
//        // Мокаем удаление в хранилище
//        doNothing().when(userDbStorage).removeFriend(1, 2);
//
//        // Act
//        userService.removeFriend(1, 2);
//
//        // Assert: проверяем, что метод проверки существования вызван для обоих пользователей
//        verify(userDbStorage, times(1)).getUserById(1);
//        verify(userDbStorage, times(1)).getUserById(2);
//        // и что удаление было передано в хранилище
//        verify(userDbStorage, times(1)).removeFriend(1, 2);
//    }
//
//
//    @Test
//    void testGetListFriends() {
//        when(userDbStorage.exist(5)).thenReturn(true);
//        when(userDbStorage.getListFriends(5)).thenReturn(List.of(new User()));
//
//        userService.getListFriends(5);
//
//        verify(userDbStorage).exist(5);
//        verify(userDbStorage).getListFriends(5);
//    }
//
//    @Test
//    void testGetMutualFriends() {
//        when(userDbStorage.exist(1)).thenReturn(true);
//        when(userDbStorage.exist(2)).thenReturn(true);
//        when(userDbStorage.getMutualFriends(1, 2)).thenReturn(List.of(new User()));
//
//        userService.getMutualFriends(1, 2);
//
//        verify(userDbStorage).exist(1);
//        verify(userDbStorage).exist(2);
//        verify(userDbStorage).getMutualFriends(1, 2);
//    }
//
//    @Test
//    void testGetUserById() {
//        User user = new User(
//                1,
//                "test@mail.com",
//                "login",
//                "name",
//                LocalDate.of(2000, 1, 1),
//                new HashSet<>()
//        );
//
//        when(userDbStorage.getUserById(1)).thenReturn(Optional.of(user));
//
//        UserDto userById = userService.getUserById(1);
//
//        assertAll(
//                () -> assertEquals(1, userById.getId()),
//                () -> assertEquals("test@mail.com", userById.getEmail()),
//                () -> assertEquals("login", userById.getLogin()),
//                () -> assertEquals("name", userById.getName()),
//                () -> assertEquals(LocalDate.of(2000, 1, 1), userById.getBirthday()),
//                () -> assertTrue(userById.getFriends().isEmpty())
//        );
//
//        verify(userDbStorage, times(1)).getUserById(1);
//    }
//}
//
