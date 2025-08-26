package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;


@Repository
@Qualifier("dbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;
    private final SimpleJdbcInsert inserter;

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
        this.inserter = new SimpleJdbcInsert(jdbc)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
    }

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String UPDATE_BY_ID = """
                UPDATE users
                   SET email    = ?,
                       login    = ?,
                       name     = ?,
                       birthday = ?
                 WHERE user_id  = ?
            """;
    private static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE user_id = ?";
    private static final String CHECK_EXIST = "SELECT COUNT(*) FROM users WHERE user_id = ?";
    private static final String ADD_FRIEND = "INSERT INTO friendships(user_id, friend_id) VALUES(?,?) ";
    private static final String GET_FRIENDS = """
                    SELECT u.*
                    FROM friendships f JOIN users u ON u.user_id=f.friend_id
                    WHERE f.user_id=?
                    ORDER BY u.user_id
            """;
    private static final String MUTUAL_FRIENDS = """
            select distinct u.*
            from friendships f1
            join friendships f2 on f1.friend_id = f2.friend_id
            join users u on u.user_id = f1.friend_id
            where f1.user_id = ? and f2.user_id = ?
            ORDER BY u.user_id
            """;
    private static final String DELETE_FRIEND = """
            DELETE FROM friendships
            WHERE user_id = ? AND friend_id = ?
            """;
//    private static final String CONFIRMATION_FRIEND = """
//            UPDATE friendships
//            SET STATUS = 'CONFIRMED'
//               WHERE user_id = ?
//                AND friend_id = ?
//                AND status = 'UNCONFIRMED'
//                        """;
//    двустороняя дружба(подтвержденная) не нужна на яндексе

//    private static final String CONFIRM_FRIEND = """
//            INSERT INTO friendships(user_id, friend_id, status)
//            SELECT ?, ?, 'CONFIRMED'
//            WHERE NOT EXISTS (
//                SELECT 1 FROM friendships WHERE user_id = ? AND friend_id = ?
//            )
//             """;


    @Override
    public Collection<User> getUsers() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(user);
        Number generatedId = inserter.executeAndReturnKey(params);
        user.setId(generatedId.intValue());
        return user;
    }

    @Override
    public User update(User newUser) {
        int rows = jdbc.update(UPDATE_BY_ID,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                Date.valueOf(newUser.getBirthday()),
                newUser.getId()
        );
        if (rows == 0) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
        return newUser;
    }

    @Override
    public void delete(User user) {
        jdbc.update(DELETE_USER_BY_ID, user.getId());
    }

    @Override
    public boolean exist(Integer id) {
        Integer count = jdbc.queryForObject(CHECK_EXIST, Integer.class, id);
        return count != null && count > 0;
    }


    @Override
    public User addFriend(Integer id, Integer friendId) {
        if (!exist(id) || !exist(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        jdbc.update(ADD_FRIEND, id, friendId);
        return getUserById(id).orElseThrow();
    }

    @Override
    public void removeFriend(Integer id, Integer friendId) {
        jdbc.update(DELETE_FRIEND, id, friendId);
    }

    @Override
    public Collection<User> getListFriends(Integer id) {
        return jdbc.query(GET_FRIENDS, mapper, id);
    }

    @Override
    public Collection<User> getMutualFriends(Integer id, Integer friendId) {
        return jdbc.query(MUTUAL_FRIENDS, mapper, id, friendId);
    }

//    @Override
//    public User confirmFriendRequest(Integer id, Integer friendId) {
//        int rows = jdbc.update(CONFIRMATION_FRIEND, id, friendId);
//        if (rows == 0) {
//            throw new NotFoundException("Запрос в друзья не найден");
//        }
//        jdbc.update(CONFIRM_FRIEND, friendId, id, friendId, id);
//        return getUserById(id).orElseThrow();
//    }
}
