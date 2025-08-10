//package ru.yandex.practicum.filmorate.storage.impl;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.context.annotation.Import;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@JdbcTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@Import({UserDbStorage.class})
//public class UserDbStorageTest {
//    private final UserDbStorage userDbStorage;
//
//    @Test
//    public void testFindUserById() {
//
//        Optional<User> userOptional = userDbStorage.getUserById(1);
//
//        assertThat(userOptional)
//                .isPresent()
//                .hasValueSatisfying(user ->
//                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
//                );
//    }
//
//    @Test
//    public void getUsers() {
//
//        Optional<User> userOptional = userDbStorage.getUsers();
//
//        assertThat(userOptional)
//                .isPresent()
//                .hasValueSatisfying(user ->
//                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
//                );
//    }
//}
