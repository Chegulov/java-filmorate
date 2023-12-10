package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User create(User user) {
        int userId = addUserToDb(user);
        user.setId(userId);
        String sqlQuery = "INSERT into relationship (user_id, friend_id) VALUES (?, ?)";
        if (!user.getFriends().isEmpty()) {
            for (int id : user.getFriends()) {
                jdbcTemplate.update(sqlQuery, userId, id);
            }
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (!dbContainsUser(user.getId())) {
            String msg = String.format("Пользователь с id=%d не найден", user.getId());
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        String sqlQuery = "UPDATE users SET " +
                "user_id=?, email=?, login=?, name=?, birthday=?";
        jdbcTemplate.update(
                sqlQuery,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());


        String sqlQ = "DELETE FROM relationship WHERE user_id=?";
        jdbcTemplate.update(sqlQ, user.getId());

        if (user.getFriends() != null && user.getFriends().size() !=0) {
            String sqlQ2 = "INSERT INTO relationship (user_id, friend_id) VALUES (?, ?)";
            user.getFriends().forEach(id -> jdbcTemplate.update(sqlQ2, user.getId(), id));
        }

        return user;
    }

    @Override
    public User getUserById(int id) {
        String sqlQuery = "SELECT * FROM users WHERE user_id=?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
        } catch (EmptyResultDataAccessException e) {
            String msg = String.format("Пользователь с id=%d не найден", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        return user;
    }

    @Override
    public User addFriend(int userId, int friendId) {
        if (!dbContainsUser(userId)) {
            String msg = String.format("Пользователь с id=%d не найден", userId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        if (!dbContainsUser(friendId)) {
            String msg = String.format("Пользователь с id=%d не найден", friendId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        if (userId == friendId) {
            String msg = "Нельзя добавиться в друзья к самому себе";
            log.info(msg);
            throw new ValidationException(msg);
        }

        String sqlQuery = "INSERT INTO relationship (user_id, friend_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } catch (DuplicateKeyException e) {
            String msg = "Нельзя добавиться в друзья дважды";
            log.info(msg);
            throw new DuplicateDataException(msg);
        }
        return getUserById(userId);
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        if (!dbContainsUser(userId)) {
            String msg = String.format("Пользователь с id=%d не найден", userId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        if (!dbContainsUser(friendId)) {
            String msg = String.format("Пользователь с id=%d не найден", friendId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        String sqlQuery = "DELETE FROM relationship where user_id=? AND friend_id=?";
        if (jdbcTemplate.update(sqlQuery, userId, friendId) == 0) {
            String msg = String.format("Пользователя с id=%d нет в друзьях у пользователя с id=%d", friendId, userId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        return getUserById(userId);
    }

    public List<Integer> getFriends(int id) {
        if (!dbContainsUser(id)) {
            String msg = String.format("Пользователь с id=%d не найден", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        String sqlQuery = "SELECT friend_id FROM relationship WHERE user_id=?";
        return jdbcTemplate.query(sqlQuery, (rs1, rowNum1) -> rs1.getInt("friend_id"), id);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();

        String sqlQuery = "SELECT friend_id FROM relationship WHERE user_id=?";
        user.getFriends().addAll(jdbcTemplate.query(
                sqlQuery,
                (rs1, rowNum1) -> rs1.getInt("friend_id"),
                user.getId())
        );
        return user;
    }

    private boolean dbContainsUser(int userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id=?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::makeUser, userId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private int addUserToDb(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
    }
}
