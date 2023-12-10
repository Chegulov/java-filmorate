package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userStorage;
    private User user;
    private User user2;

    @BeforeEach
    public void beforeEach() {
        userStorage = new UserDbStorage(jdbcTemplate);
        user =  User.builder()
                .email("123@321.com")
                .login("123123")
                .birthday(LocalDate.of(2001, 1, 1))
                .name("Jonn")
                .build();
        user2 = User.builder()
                .email("666@321.com")
                .login("newlogin")
                .birthday(LocalDate.of(2011, 1, 1))
                .name("JonnNewman")
                .build();
    }

    @Test
    public void shouldAddAndGetUser() {
        userStorage.create(user);

        User savedUser = userStorage.getUserById(1);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void shouldThrowExceptionWhenWrongId() {
        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> userStorage.getUserById(1)
        );

        assertEquals(exception.getMessage(), "Пользователь с id=1 не найден");
    }

    @Test
    public void shouldUpdateUser() {
        userStorage.create(user);
        user2.setId(1);

        userStorage.update(user2);

        User savedUser = userStorage.getUserById(1);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user2);
    }

    @Test
    public void shouldNotUpdateAndThrowExceptionWhenWrongId() {
        userStorage.create(user);
        user2.setId(2);

        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> userStorage.update(user2)
        );

        assertEquals(exception.getMessage(), "Пользователь с id=2 не найден");
    }

    @Test
    public void shouldGetUsers() {
        userStorage.create(user);
        userStorage.create(user2);

        List<User> savedUsers = userStorage.getUsers();

        assertThat(savedUsers)
                .isNotNull()
                .isEqualTo(List.of(user, user2));
    }

    @Test
    public void shouldAddFriend() {
        userStorage.create(user);
        userStorage.create(user2);

        userStorage.addFriend(1, 2);
        List<Integer> savedFriends = userStorage.getFriends(1);

        assertThat(savedFriends)
                .isNotNull()
                .isEqualTo(List.of(2));
    }

    @Test
    public void shouldThrowExceptionWhenDoubleFriend() {
        userStorage.create(user);
        userStorage.create(user2);

        userStorage.addFriend(1, 2);

        final DuplicateDataException exception = assertThrows(
                DuplicateDataException.class,
                () -> userStorage.addFriend(1, 2)
        );

        assertEquals(exception.getMessage(), "Нельзя добавиться в друзья дважды");
    }

    @Test
    public void shouldThrowExceptionWhenSelfFriend() {
        userStorage.create(user);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.addFriend(1, 1)
        );

        assertEquals(exception.getMessage(), "Нельзя добавиться в друзья к самому себе");
    }

    @Test
    public void shouldDeleteFriend() {
        userStorage.create(user);
        userStorage.create(user2);

        userStorage.addFriend(1, 2);
        userStorage.deleteFriend(1, 2);
        List<Integer> savedFriends = userStorage.getFriends(1);

        assertNotNull(savedFriends);
        assertTrue(savedFriends.isEmpty());
    }

    @Test
    public void shouldThrowExceptionWhenNotInFriends() {
        userStorage.create(user);
        userStorage.create(user2);

        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> userStorage.deleteFriend(1, 2)
        );

        assertEquals(exception.getMessage(), "Пользователя с id=2 нет в друзьях у пользователя с id=1");
    }
}