package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    // уникальные значения, чтобы не конфликтовать с data.sql (где уже есть u1/u2/u3)
    private User newUser(String email, String login, String name) {
        return new User(
                null,
                email,
                login,
                name,
                LocalDate.of(1990, 1, 1),
                new HashSet<>()
        );
    }

    @Test
    @DisplayName("getUserById(1): читает пользователя, которого положил data.sql")
    void testFindUserById() {
        Optional<User> opt = userDbStorage.getUserById(1);
        assertThat(opt)
                .isPresent()
                .get()
                .satisfies(u -> {
                    assertThat(u.getId()).isEqualTo(1);
                    assertThat(u.getEmail()).isEqualTo("u1@mail.com");
                    assertThat(u.getLogin()).isEqualTo("u1");
                });
    }

    @Test
    @DisplayName("save → getById: ID присваивается, запись читается")
    void save_and_getById() {
        var saved = userDbStorage.save(newUser("alice1@mail.com", "alice1", "Alice"));
        var fromDb = userDbStorage.getUserById(saved.getId()).orElseThrow();

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(fromDb.getEmail()).isEqualTo("alice1@mail.com"),
                () -> assertThat(fromDb.getLogin()).isEqualTo("alice1"),
                () -> assertThat(fromDb.getName()).isEqualTo("Alice")
        );
    }

    @Test
    @DisplayName("update обновляет поля пользователя")
    void update_user() {
        var u = userDbStorage.save(newUser("bob1@mail.com", "bob1", "Bob"));
        u.setName("Bobby");
        u.setEmail("bobby1@mail.com");

        var updated = userDbStorage.update(u);
        var fromDb = userDbStorage.getUserById(updated.getId()).orElseThrow();

        assertAll(
                () -> assertThat(fromDb.getName()).isEqualTo("Bobby"),
                () -> assertThat(fromDb.getEmail()).isEqualTo("bobby1@mail.com")
        );
    }

    @Test
    @DisplayName("getUsers возвращает всех пользователей (увеличивается после 2-х save)")
    void get_all_users() {
        int before = userDbStorage.getUsers().size();

        userDbStorage.save(newUser("u4@mail.com", "u4", "U4"));
        userDbStorage.save(newUser("u5@mail.com", "u5", "U5"));

        var all = userDbStorage.getUsers();
        assertAll(
                () -> assertThat(all.size()).isEqualTo(before + 2),
                () -> assertThat(all).extracting("login").contains("u4", "u5")
        );
    }

    @Test
    @DisplayName("addFriend: в getListFriends до подтверждения пусто (возвращает только CONFIRMED)")
    void addFriend_then_list_is_empty_until_confirm() {
        var a = userDbStorage.save(newUser("a1@mail.com", "a1", "A1")); // requester
        var b = userDbStorage.save(newUser("b1@mail.com", "b1", "B1")); // target

        userDbStorage.addFriend(a.getId(), b.getId()); // создаёт UNCONFIRMED a->b

        // getListFriends возвращает только CONFIRMED — значит, до подтверждения пусто
        var friendsOfA = userDbStorage.getListFriends(a.getId());
        var friendsOfB = userDbStorage.getListFriends(b.getId());

        assertAll(
                () -> assertThat(friendsOfA).isEmpty(),
                () -> assertThat(friendsOfB).isEmpty()
        );
    }

    @Test
    @DisplayName("confirmFriendRequest подтверждает заявку: после — друг появляется у инициатора (и опционально у получателя)")
    void confirm_friend_request() {
        var requester = userDbStorage.save(newUser("req@mail.com", "req", "Requester")); // инициатор
        var target    = userDbStorage.save(newUser("tgt@mail.com", "tgt", "Target"));    // получатель

        userDbStorage.addFriend(requester.getId(), target.getId());           // запись requester -> target, UNCONFIRMED
        userDbStorage.confirmFriendRequest(requester.getId(), target.getId()); // подтверждаем именно (requester, target)

        var friendsOfRequester = userDbStorage.getListFriends(requester.getId());
        assertThat(friendsOfRequester).extracting("id").containsExactly(target.getId());

        var friendsOfTarget = userDbStorage.getListFriends(target.getId());
        assertThat(friendsOfTarget).extracting("id").containsExactly(requester.getId());
    }

    @Test
    @DisplayName("getMutualFriends: общий подтверждённый друг возвращается")
    void mutual_friends() {
        var a = userDbStorage.save(newUser("a2@mail.com", "a2", "A2"));
        var b = userDbStorage.save(newUser("b2@mail.com", "b2", "B2"));
        var c = userDbStorage.save(newUser("c2@mail.com", "c2", "C2")); // общий

        userDbStorage.addFriend(c.getId(), a.getId());
        userDbStorage.confirmFriendRequest(c.getId(), a.getId());

        userDbStorage.addFriend(c.getId(), b.getId());
        userDbStorage.confirmFriendRequest(c.getId(), b.getId());

        var mutual = userDbStorage.getMutualFriends(a.getId(), b.getId());
        assertThat(mutual).extracting("id").containsExactly(c.getId());
    }

    @Test
    @DisplayName("removeFriend удаляет дружбу в обе стороны")
    void remove_friend() {
        var u1 = userDbStorage.save(newUser("r1@mail.com", "r1", "R1"));
        var u2 = userDbStorage.save(newUser("r2@mail.com", "r2", "R2"));

        userDbStorage.addFriend(u2.getId(), u1.getId());
        userDbStorage.confirmFriendRequest(u2.getId(), u1.getId());

        userDbStorage.removeFriend(u1.getId(), u2.getId());

        var mutual = userDbStorage.getMutualFriends(u1.getId(), u2.getId());
        assertThat(mutual).isEmpty();
    }
}
